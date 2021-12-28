package dev.omnicron.bots.server_manager.buttons

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.interactions.components.Button

class ButtonRestartServer: IMessageButton {

    private lateinit var button: Button

    override fun getName() = "Stop"

    override fun getButtonType(): IMessageButton.IButtonType {
        return IMessageButton.IButtonType.PRIMARY
    }

    override fun onButtonClicked(event: ButtonClickEvent) {
        event.message.reply("Got it, server stopped!").queue()
    }

    override fun setButton(button: Button) {
        this.button = button
    }

    override fun cancel() {
        TODO("Not yet implemented") // Need to find a way to easily set a button as disabled and update the embed
    }
}