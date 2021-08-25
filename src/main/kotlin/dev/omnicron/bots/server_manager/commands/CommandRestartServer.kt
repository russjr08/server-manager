package dev.omnicron.bots.server_manager.commands

import com.mattmalec.pterodactyl4j.client.entities.PteroClient
import dev.omnicron.bots.server_manager.ServerManager
import net.dv8tion.jda.api.entities.Message

class CommandRestartServer(private val manager: ServerManager, private val pteroApi: PteroClient): ICommand {

    override fun getName(): String = "restart"

    override fun run(args: List<String>, message: Message) {
        if(args.isEmpty()) {
            message.addReaction("❌").queue()
            message.reply("You did not specify a server name!").queue()
            return
        }

        if(!manager.hasPermissionType(message, ServerManager.MinecraftPermissionType.MODERATOR)) {
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