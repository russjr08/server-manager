package dev.omnicron.bots.server_manager.commands

import net.dv8tion.jda.api.entities.Message

class CommandTest: ICommand {

    override fun getName(): String = "test"

    override fun run(args: List<String>, message: Message) {
        message.reply("Message received, you specified the following: ${args.toString()}").queue {
            message.addReaction("😁").queue()
        }
    }

}