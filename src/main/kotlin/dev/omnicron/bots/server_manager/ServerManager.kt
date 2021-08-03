package dev.omnicron.bots.server_manager

import dev.omnicron.bots.server_manager.util.ConfigException
import dev.omnicron.bots.server_manager.util.debug
import io.github.cdimascio.dotenv.dotenv
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder

class ServerManager {
    lateinit var token: String
    lateinit var jda: JDA

    fun start() {
        debug("Please wait while I gather my bearings... Initializing now!")
        debug("Beginning system checks.")
        debug("Checking to see if I was given my ticket to Discord...")

        val dotenv = dotenv()
        if(dotenv["BOT_TOKEN"] == null) {
            throw ConfigException("You must specify a BOT_TOKEN in your .env file")
        }

        token = dotenv["BOT_TOKEN"]

        debug("Looks like I do have a ticket - but is it valid?")

        jda = JDABuilder.createDefault(token).build()

        debug("Validity verified.")
    }
}

