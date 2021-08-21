package dev.omnicron.bots.server_manager.commands

import com.mattmalec.pterodactyl4j.client.entities.PteroClient
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import java.awt.Color

class CommandListServers(private val pteroApi: PteroClient): ICommand {

    override fun getName(): String = "servers"

    override fun run(args: List<String>, message: Message) {
        pteroApi.retrieveServers().executeAsync { servers ->
            val embedBuilder = EmbedBuilder()
            embedBuilder.setColor(Color.RED)
            embedBuilder.setTitle("List of Registered Servers")
            embedBuilder.setDescription("Each item is the server's name, and the node it's present on")

            servers.forEach { server ->
                embedBuilder.addField(server.name, server.node, false)
            }

            embedBuilder.setFooter("ServerManager - Russell Richardson")

            message.channel.sendMessage(embedBuilder.build()).queue()
        }
    }
}