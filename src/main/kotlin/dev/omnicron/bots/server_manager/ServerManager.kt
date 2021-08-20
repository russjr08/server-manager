package dev.omnicron.bots.server_manager

import com.mattmalec.pterodactyl4j.PteroBuilder
import com.mattmalec.pterodactyl4j.client.entities.PteroClient
import dev.omnicron.bots.server_manager.util.ConfigException
import dev.omnicron.bots.server_manager.util.debug
import io.github.cdimascio.dotenv.dotenv
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity

class ServerManager {
    lateinit var token: String
    lateinit var jda: JDA
    private val dotenv = dotenv()
    private var COMMAND_PREFIX = "-"

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

        jda = JDABuilder.createDefault(token).build()
        pteroApi = PteroBuilder.createClient(dotenv["PTERODACTYL_URL"], dotenv["PTERODACTYL_API_KEY"])

        pteroApi.retrieveServers().executeAsync { servers ->
            debug("I've connected to Pterodactyl and found the following servers:")
            servers.forEach { server -> debug("${server.name} -> ${server.node}") }
        }

        debug("Validity verified.")
        setup()
    }

    private fun setup() {
        if(dotenv["COMMAND_PREFIX"] != null) {
            COMMAND_PREFIX = dotenv["COMMAND_PREFIX"]
        }

        jda.presence.activity = Activity.watching("over Minecraft servers!")
    }
}

