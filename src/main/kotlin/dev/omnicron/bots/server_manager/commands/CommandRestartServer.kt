package dev.omnicron.bots.server_manager.commands

import com.mattmalec.pterodactyl4j.PteroAction
import com.mattmalec.pterodactyl4j.client.entities.ClientServer
import com.mattmalec.pterodactyl4j.client.entities.PteroClient
import dev.omnicron.bots.server_manager.ReactionQueueItem
import dev.omnicron.bots.server_manager.ServerManager
import dev.omnicron.bots.server_manager.ServerQueueAction
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent

class CommandRestartServer(private val manager: ServerManager, private val pteroApi: PteroClient): ICommand {

    override fun getName(): String = "restart"

    override fun run(args: List<String>, message: Message) {
        if(args.isEmpty()) {
            message.addReaction("❌").queue()
            message.reply("You did not specify a server name!").queue()
            return
        }

        if(!manager.hasPermissionType(message.member!!, ServerManager.PermissionType.MODERATOR)) {
            message.addReaction("❌").queue()
            message.reply("You do not have permission to run this command!").queue()
            return
        }

        val serverName = args.joinToString(" ").lowercase()

        pteroApi.retrieveServers().executeAsync { servers ->
            val matchedServer = servers.firstOrNull { server -> server.name.lowercase() == serverName }
            if(matchedServer == null) {
                message.addReaction("❌").queue()
                message.reply("There was no server found by that name!").queue()
                return@executeAsync
            }

            matchedServer.restart().executeAsync { _ ->
                message.addReaction("✅").queue()
                message.reply("${matchedServer.name} has been restarted!").queue()
            }
        }

    }

}

class RestartQueueItem(private val message: Message,
                       private val manager: ServerManager,
                       private val action: ServerQueueAction<PteroAction<Void>, ClientServer>,
                       private val done: (item: StopQueueItem) -> Unit): ReactionQueueItem<PteroAction<Void>, ClientServer> {

    override fun run(event: MessageReactionAddEvent) {
        TODO("Not yet implemented")
    }

    override fun isFor(): Message {
        TODO("Not yet implemented")
    }

    override fun getAction(): ServerQueueAction<PteroAction<Void>, ClientServer> {
        TODO("Not yet implemented")
    }

    override fun onCancelled() {
        TODO("Not yet implemented")
    }

}

class RestartAction: ServerQueueAction<PteroAction<Void>, ClientServer> {
    override fun performOnServer(): PteroAction<Void> {
        TODO("Not yet implemented")
    }

    override fun actingUpon(): ClientServer {
        TODO("Not yet implemented")
    }

}