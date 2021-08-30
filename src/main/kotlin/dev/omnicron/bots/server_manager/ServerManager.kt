package dev.omnicron.bots.server_manager

import com.mattmalec.pterodactyl4j.PteroBuilder
import com.mattmalec.pterodactyl4j.client.entities.PteroClient
import dev.omnicron.bots.server_manager.commands.CommandListServers
import dev.omnicron.bots.server_manager.commands.CommandRestartServer
import dev.omnicron.bots.server_manager.commands.CommandTest
import dev.omnicron.bots.server_manager.commands.ICommand
import dev.omnicron.bots.server_manager.util.ConfigException
import dev.omnicron.bots.server_manager.util.debug
import io.github.cdimascio.dotenv.Dotenv
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class ServerManager: ListenerAdapter() {
    lateinit var jda: JDA

    private lateinit var MC_ADMIN_ROLE_ID: String
    private lateinit var MC_MOD_ROLE_ID: String

    private lateinit var dotenv: Dotenv
    private var COMMAND_PREFIX = "-"
    private var commands = ArrayList<ICommand>()

    private lateinit var pteroApi: PteroClient

    fun start() {

        dotenv = Dotenv.configure().directory("./data").load()

        debug("Please wait while I gather my bearings... Initializing now!")
        debug("Beginning system checks.")
        debug("Checking to see if I was given my ticket to Discord...")

        if(dotenv["BOT_TOKEN"] == null) {
            throw ConfigException("You must specify a BOT_TOKEN in your .env file")
        }

        if(dotenv["PTERODACTYL_API_KEY"] == null) {
            throw ConfigException("You must specify a PTERODACTYL_API_KEY in your .env file")
        }

        if(dotenv["PTERODACTYL_URL"] == null) {
            throw ConfigException("You must specify a PTERODACTYL_URL in your .env file")
        }

        if(dotenv["DISCORD_MC_ADMIN_ID"] != null) {
            MC_ADMIN_ROLE_ID = dotenv["DISCORD_MC_ADMIN_ID"]
        }

        if(dotenv["DISCORD_MC_MOD_ID"] != null) {
            MC_MOD_ROLE_ID = dotenv["DISCORD_MC_MOD_ID"]
        }

        val token: String = dotenv["BOT_TOKEN"]

        debug("Looks like I do have a ticket - but is it valid?")

        jda = JDABuilder.createDefault(token).addEventListeners(this).build()
        pteroApi = PteroBuilder.createClient(dotenv["PTERODACTYL_URL"], dotenv["PTERODACTYL_API_KEY"])

        pteroApi.retrieveServers().executeAsync { servers ->
            debug("I've connected to Pterodactyl and found the following servers:")
            servers.forEach { server -> debug("${server.name} -> ${server.node}") }
        }


    }

    private fun setup() {
        if(dotenv["COMMAND_PREFIX"] != null) {
            COMMAND_PREFIX = dotenv["COMMAND_PREFIX"]
        }

        commands.add(CommandListServers(pteroApi))
        commands.add(CommandTest())
        commands.add(CommandRestartServer(this, pteroApi))

        jda.presence.activity = Activity.watching("over Minecraft servers!")
    }

    override fun onReady(event: ReadyEvent) {
        debug("Validity verified.")
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

    fun hasPermissionType(message: Message, permissionType: MinecraftPermissionType): Boolean {
        var moderatorRole: Role?
        var administratorRole: Role?

        if(message.guild.getMember(message.author)!!.hasPermission(Permission.ADMINISTRATOR)) {
            return true
        }

        if(!MC_ADMIN_ROLE_ID.isNullOrEmpty()) {
            message.guild.getRoleById(MC_ADMIN_ROLE_ID).let { role -> administratorRole = role }
        } else {
            administratorRole = null
            debug("You haven't set an DISCORD_MC_ADMIN_ID in your .env file," +
                    " permission check will fallback to whether the user has ADMINISTRATOR")
        }

        if(!MC_MOD_ROLE_ID.isNullOrEmpty()) {
            message.guild.getRoleById(MC_MOD_ROLE_ID).let { role -> moderatorRole = role }
        } else {
            moderatorRole = null
            debug("You haven't set an DISCORD_MC_MOD_ID in your .env file," +
                    " permission check will fallback to whether the user has ADMINISTRATOR")
        }

        if(moderatorRole != null && administratorRole != null) {
            if(permissionType == MinecraftPermissionType.MODERATOR) {
                if(message.guild.getMember(message.author)!!.roles.contains(moderatorRole)
                    || message.guild.getMember(message.author)!!.roles.contains(administratorRole)) {
                    return true
                }
            }

            if(permissionType == MinecraftPermissionType.ADMINISTRATOR) {
                if(message.guild.getMember(message.author)!!.roles.contains(administratorRole)) {
                    return true
                }
            }
        }

        return false
    }

    enum class MinecraftPermissionType {
        MODERATOR, ADMINISTRATOR
    }

}

