package dev.omnicron.bots.server_manager.commands

import com.mattmalec.pterodactyl4j.PteroAction
import com.mattmalec.pterodactyl4j.client.entities.ClientServer
import com.mattmalec.pterodactyl4j.client.entities.PteroClient
import dev.omnicron.bots.server_manager.Helpers
import dev.omnicron.bots.server_manager.ReactionQueueItem
import dev.omnicron.bots.server_manager.ServerManager
import dev.omnicron.bots.server_manager.ServerQueueAction
import dev.omnicron.bots.server_manager.util.ActionTypeResult
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import kotlin.concurrent.schedule

class CommandRestartServer(private val manager: ServerManager, private val pteroApi: PteroClient): ICommand {

    override fun getName(): String = "restart"

    override fun run(args: List<String>, message: Message) {
        /** If author of the message is a moderator instead of an administrator,
         *  require at least three moderators to confirm via a reaction
         */

        if(!Helpers.checkArguments(1, message, args)) {
            return
        }

        if(!manager.hasPermissionType(message.member!!, ServerManager.PermissionType.MODERATOR)
            && !manager.hasPermissionType(message.member!!, ServerManager.PermissionType.ADMINISTRATOR)) {
            Helpers.sendInvalidPermissionsEmbed(message)
            return
        }

        val serverName = args.joinToString(" ").lowercase()

        pteroApi.retrieveServersByName(serverName, false).executeAsync { servers ->
            val server: ClientServer

            if(servers.size == 0) {
                Helpers.sendServersNotFoundEmbed(message)
                return@executeAsync
            } else if(servers.size > 1) {
                val matched = servers.find { it -> it.name.lowercase() == serverName }
                if(matched != null) {
                    server = matched
                } else {
                    Helpers.sendTooManyServersMatchedEmbed(message)
                    return@executeAsync
                }
            } else {
                server = servers.first()
            }

            if(!manager.checkIfQueueActionExistsForServer(server)) {
                buildRestartAction(message, server)
            } else {
                Helpers.sendActionAlreadyPendingEmbed(message, server)
            }

        }

    }

    private fun buildRestartAction(message: Message, server: ClientServer) {
        if(manager.hasPermissionType(message.member!!, ServerManager.PermissionType.ADMINISTRATOR)) {
            val embed = Helpers.getActionConfirmationEmbed(server.name, "Restart Server",
                ActionTypeResult.PENDING, false)
            sendRestartAction(message, embed, server)

        } else if(manager.hasPermissionType(message.member!!, ServerManager.PermissionType.MODERATOR)) {
            val embed = Helpers.getActionConfirmationEmbed(server.name, "Restart Server",
                ActionTypeResult.PENDING, true)
            sendRestartAction(message, embed, server)
        }
    }

    private fun sendRestartAction(message: Message, embed: MessageEmbed, server: ClientServer) {
        var action: RestartQueueItem

        message.channel.sendMessageEmbeds(embed).queue { originalMessage ->
            originalMessage.addReaction("✅").queue()
            action = RestartQueueItem(originalMessage, manager, RestartServerAction(server)) { queueItem ->
                manager.unSubscribeToReactions(queueItem)
            }
            manager.subscribeToReactions(action)
            java.util.Timer().schedule(20000) {
                action.onCancelled()
            }

        }
    }

}

class RestartQueueItem(private val message: Message,
                       private val manager: ServerManager,
                       private val action: ServerQueueAction<PteroAction<Void>, ClientServer>,
                       private val done: (item: RestartQueueItem) -> Unit): ReactionQueueItem<PteroAction<Void>, ClientServer> {

    private var isCompleted = false

    override fun run(event: MessageReactionAddEvent) {
        event.retrieveMessage().queue { messageFromEvent ->
            if(manager.hasPermissionType(event.member!!, ServerManager.PermissionType.ADMINISTRATOR)) {
                confirmed(event)
            } else if(manager.hasPermissionType(event.member!!, ServerManager.PermissionType.MODERATOR)) {
                event.retrieveMessage().queue { _ ->
                    if((messageFromEvent.reactions.first().count - 1) > 1) { // Remove one, as the bot's initial reaction will count here
                        confirmed(event)
                    }
                }
            } else {
                event.reaction.removeReaction(event.user!!)
            }
        }
    }

    private fun confirmed(event: MessageReactionAddEvent) {
        action.performOnServer().executeAsync {
            message.addReaction("☑️").queue()
            message.reply("${this.action.actingUpon().name} has been restarted!").queue()
            done(this)
            event.retrieveMessage().queue { message ->
                if(message.embeds.size >= 1) {
                    message.editMessageEmbeds(
                        Helpers.getActionConfirmationEmbed(action.actingUpon().name,
                        "Restart Server", ActionTypeResult.CONFIRMED)).queue()
                }
            }
        }
        isCompleted = true
    }

    override fun onCancelled() {
        if(isCompleted) return // Do not attempt to perform anything if this task is already complete
        val embed = Helpers.getActionConfirmationEmbed(action.actingUpon().name,
            "Restart Server", ActionTypeResult.FAILED, false)

        // Update the message in Discord to notify that this task has expired/failed, then unsubscribe
        message.editMessageEmbeds(embed).queue {
            message.clearReactions().queue() {
                message.addReaction("❌").queue()
                isCompleted = true
                manager.unSubscribeToReactions(this)
            }
        }
    }

    override fun isFor() = this.message

    override fun getAction() = this.action

}

class RestartServerAction(private val server: ClientServer): ServerQueueAction<PteroAction<Void>, ClientServer> {
    override fun performOnServer(): PteroAction<Void> {
        return this.server.restart()
    }

    override fun actingUpon() = this.server

}