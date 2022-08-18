package dev.omnicron.bots.server_manager

import com.jagrosh.jdautilities.command.CommandClientBuilder
import com.mattmalec.pterodactyl4j.PteroAction
import com.mattmalec.pterodactyl4j.PteroBuilder
import com.mattmalec.pterodactyl4j.client.entities.ClientServer
import com.mattmalec.pterodactyl4j.client.entities.PteroClient
import dev.omnicron.bots.server_manager.commands.*
import dev.omnicron.bots.server_manager.util.ConfigException
import dev.omnicron.bots.server_manager.util.debug
import io.github.cdimascio.dotenv.Dotenv
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.jetbrains.annotations.NotNull
import java.io.File
import kotlin.system.exitProcess

class ServerManager: ListenerAdapter() {
    lateinit var jda: JDA

    private lateinit var MC_ADMIN_ROLE_ID: String
    private lateinit var MC_MOD_ROLE_ID: String

    private lateinit var dotenv: Dotenv
    private var COMMAND_PREFIX = "-"
    private var commands = ArrayList<ICommand>()
    private val reactionQueueItems = ArrayList<ReactionQueueItem<PteroAction<Void>, ClientServer>>()
    private lateinit var pteroApi: PteroClient

    fun start() {
        val envFileExists = File("data/.env").exists()

        if(!envFileExists) {
            System.err.println("Oh dear... You do not have a configuration file! This should be in '/data/.env'")
            exitProcess(1)
        }

        dotenv = Dotenv.configure().directory("./data").load()

        debug("Please wait while I gather my bearings... Initializing now!")
        debug("Beginning system checks.")
        debug("Checking to see if I was given my ticket to Discord...")

        if(dotenv["BOT_TOKEN"] == null) {
            throw ConfigException("You must specify a BOT_TOKEN in your .env file")
        }

        if(dotenv["PTERODACTYL_API_KEY"] != null) {
//            throw ConfigException("You must specify a PTERODACTYL_API_KEY in your .env file")
            debug("You have a PTERODACTYL_API_KEY defined in your .env file, this setting has now been" +
                    " deprecated and can be safely removed.")
        }

        if(dotenv["PTERODACTYL_URL"] != null) {
//            throw ConfigException("You must specify a PTERODACTYL_URL in your .env file")
            debug("You have a PTERODACTYL_URL defined in your .env file, this setting has now been" +
                    " deprecated and can be safely removed.")
        }

        if(dotenv["DISCORD_OWNER_ID"] == null) {
            throw ConfigException("You must specify a DISCORD_OWNER_ID in your .env file")
        }

        if(dotenv["DISCORD_MC_ADMIN_ID"] != null) {
            MC_ADMIN_ROLE_ID = dotenv["DISCORD_MC_ADMIN_ID"]
            debug("You have a DISCORD_MC_ADMIN_ID defined in your .env file, this setting has now been" +
                    " deprecated and can be safely removed.")
        }

        if(dotenv["DISCORD_MC_MOD_ID"] != null) {
            MC_MOD_ROLE_ID = dotenv["DISCORD_MC_MOD_ID"]
            debug("You have a DISCORD_MC_MOD_ID defined in your .env file, this setting has now been" +
                    " deprecated and can be safely removed.")
        }

        if(dotenv["VERBOSE_LOGGING"] != null) {
            debug("Verbose logging has been enabled! Remove 'VERBOSE_LOGGING' from your .env file to disable!")
        }

        val token: String = dotenv["BOT_TOKEN"]

        debug("Looks like I do have a ticket - but is it valid?")

        val commandClientBuilder = CommandClientBuilder()
        commandClientBuilder.addSlashCommand(ServerConfigCommand())
        commandClientBuilder.setOwnerId(dotenv["DISCORD_OWNER_ID"])

        // A bit silly, but this overrides the base JDA Activity if you don't set it, with a "Ping for help" message...
        commandClientBuilder.setActivity(Activity.watching("over Minecraft servers!"))

        jda = JDABuilder.createDefault(token).addEventListeners(this, commandClientBuilder.build()).build()
        pteroApi = PteroBuilder.createClient(dotenv["PTERODACTYL_URL"], dotenv["PTERODACTYL_API_KEY"])

        // TODO: Remove this as it is not applicable due to it being a per-server setting
        pteroApi.retrieveServers().executeAsync({ servers ->
            run {
                debug("I've connected to Pterodactyl and found the following servers:")
                servers.forEach { server -> debug("${server.name} -> ${server.node}") }
            }
        }, { failure ->
            run {
                debug("I was unable to reach out to your Pterodactyl Panel for some reason!")
                error(failure.toString())
            }
        })

    }

    private fun setup() {
        if(dotenv["COMMAND_PREFIX"] != null) {
            COMMAND_PREFIX = dotenv["COMMAND_PREFIX"]
        }

        commands.add(CommandListServers(pteroApi))
        commands.add(CommandTest())
        commands.add(CommandRestartServer(this, pteroApi))
        commands.add(CommandStopServer(this, pteroApi))

        jda.presence.activity = Activity.watching("over Minecraft servers!")
    }

    fun subscribeToReactions(item: ReactionQueueItem<PteroAction<Void>, ClientServer>) {
        reactionQueueItems.add(item)
    }

    fun unSubscribeToReactions(item: ReactionQueueItem<PteroAction<Void>, ClientServer>) {
        reactionQueueItems.remove(item)
    }

    fun checkIfQueueActionExistsForServer(server: ClientServer): Boolean {
        reactionQueueItems.forEach { item ->
            if(item.getAction().actingUpon().identifier == server.identifier) {
                return true
            }
        }
        return false
    }

    override fun onReady(event: ReadyEvent) {
        debug("Validity verified. Connected to Discord as " +
                "${event.jda.selfUser.name}#${event.jda.selfUser.discriminator}!")
        setup()
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        val message = event.message
        if(message.contentRaw.startsWith(COMMAND_PREFIX)) {
            val commandPieces = message.contentRaw.split(" ")
            val commandName = commandPieces[0].replace(COMMAND_PREFIX, "")
            val commandArgs = commandPieces.filter { part -> part != commandPieces[0] }
            val command = commands.firstOrNull { command -> command.getName() == commandName }

            command?.run(commandArgs, message)
        }
    }
    
    @NotNull // Configuration should never be null, as the application exits when it is found.
    fun getConfig() = this.dotenv

    fun hasPermissionType(member: Member, permissionType: PermissionType): Boolean {
        var moderatorRole: Role?
        var administratorRole: Role?

        if(member.hasPermission(Permission.ADMINISTRATOR)) {
            return true
        }

        if(MC_ADMIN_ROLE_ID.isNotEmpty()) {
            member.guild.getRoleById(MC_ADMIN_ROLE_ID).let { role -> administratorRole = role }
        } else {
            administratorRole = null
            debug("You haven't set an DISCORD_MC_ADMIN_ID in your .env file," +
                    " permission check will fallback to whether the user has ADMINISTRATOR")
        }

        if(MC_MOD_ROLE_ID.isNotEmpty()) {
            member.guild.getRoleById(MC_MOD_ROLE_ID).let { role -> moderatorRole = role }
        } else {
            moderatorRole = null
            debug("You haven't set an DISCORD_MC_MOD_ID in your .env file," +
                    " permission check will fallback to whether the user has ADMINISTRATOR")
        }

        if(moderatorRole != null && administratorRole != null) {
            if(permissionType == PermissionType.MODERATOR) {
                if(member.roles.contains(moderatorRole)
                    || member.roles.contains(administratorRole)) {
                    return true
                }
            }

            if(permissionType == PermissionType.ADMINISTRATOR) {
                if(member.roles.contains(administratorRole)) {
                    return true
                }
            }
        }

        return false
    }

    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        if(event.userId == this.jda.selfUser.id) {
            return
        }

        reactionQueueItems.stream().filter { it.isFor().id == event.messageId }.findFirst().ifPresent() { item ->
            item.run(event)
        }
    }

    enum class PermissionType {
        MODERATOR, ADMINISTRATOR
    }

}

