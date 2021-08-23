package dev.omnicron.bots.server_manager

import com.mattmalec.pterodactyl4j.PteroBuilder
import com.mattmalec.pterodactyl4j.client.entities.PteroClient
import dev.omnicron.bots.server_manager.commands.CommandListServers
import dev.omnicron.bots.server_manager.commands.CommandTest
import dev.omnicron.bots.server_manager.commands.ICommand
import dev.omnicron.bots.server_manager.util.ConfigException
import dev.omnicron.bots.server_manager.util.debug
import io.github.cdimascio.dotenv.dotenv
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class ServerManager: ListenerAdapter() {
    lateinit var token: String
    lateinit var jda: JDA
    private val dotenv = dotenv()
    private var COMMAND_PREFIX = "-"
    private var commands = ArrayList<ICommand>()

    private lateinit var pteroApi: PteroClient

    fun start() {
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

        token = dotenv["BOT_TOKEN"]

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
}

