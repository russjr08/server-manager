package dev.omnicron.bots.server_manager.commands

import net.dv8tion.jda.api.entities.Message

interface ICommand {

    /**
     * Returns the name of the command - this should not include the command prefix
     */
    fun getName(): String

    /**
     * The "meat and potatoes" of the command
     * @param args The list of arguments being passed to this command
     * @param message The original Discord message that prompted the execution of this command
     */
    fun run(args: List<String>, message: Message)


}