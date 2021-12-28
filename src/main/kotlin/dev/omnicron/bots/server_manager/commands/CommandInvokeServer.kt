package dev.omnicron.bots.server_manager.commands

import com.mattmalec.pterodactyl4j.client.entities.ClientServer
import com.mattmalec.pterodactyl4j.client.entities.PteroClient
import dev.omnicron.bots.server_manager.Helpers
import dev.omnicron.bots.server_manager.ServerManager
import dev.omnicron.bots.server_manager.buttons.IMessageButton
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.interactions.components.Button
import net.dv8tion.jda.api.interactions.components.ButtonStyle
import java.awt.Color

/**
 * This class handles showing the options that can be
 * invoked on a server, such as start/restart/stop/kill
 */
class CommandInvokeServer(private val manager: ServerManager, private val pteroApi: PteroClient): ICommand {
    override fun getName(): String = "server"

    override fun run(args: List<String>, message: Message) {
        if(!Helpers.checkArguments(1, message, args)) {
            return
        }

        val serverName = args.joinToString(" ").lowercase()

        pteroApi.retrieveServersByName(serverName, false).executeAsync { servers ->
            if(servers.size == 0) {
                Helpers.sendServersNotFoundEmbed(message)
                return@executeAsync
            } else if(servers.size > 1) { // Check further to see if there is an exact match, otherwise error out
                val matchedServer = servers.first { it.name.lowercase() == serverName }

                if(matchedServer != null) {
                    addButtonsToServer(message, matchedServer)
                } else {
                    Helpers.sendTooManyServersMatchedEmbed(message)
                }
            } else { // There is only one server, which we'll utilize
                addButtonsToServer(message, servers.first())
            }
        }

    }

    private fun addButtonsToServer(message: Message, server: ClientServer) {
        val embed = EmbedBuilder()
            .setTitle("Invoke an Action")
            .setColor(Color.YELLOW)
            .setDescription("Sure thing! Select an Action to invoke on ${server.name} below!\n" +
                    "Selected Server: ${server.name}\n" +
                    "Invocation Status: Select an Action")
            .build()

        val buttons = ArrayList<Button>()
        manager.getInvokableActions().forEach { action ->
            val button: Button = when(action.getButtonType()) {
                IMessageButton.IButtonType.SUCCESS -> Button.success(server.internalId, action.getName())
                IMessageButton.IButtonType.PRIMARY -> Button.primary(server.internalId, action.getName())
                IMessageButton.IButtonType.DANGER -> Button.danger(server.internalId, action.getName())
                IMessageButton.IButtonType.DISABLED -> Button.of(ButtonStyle.UNKNOWN, server.internalId, action.getName())
            }
            action.setButton(button)
            buttons.add(button)
        }

        buttons.add(Button.danger("die", "Make Die"))
        message.channel.sendMessageEmbeds(embed).setActionRow(buttons).queue()

    }
}