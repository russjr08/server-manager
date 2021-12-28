package dev.omnicron.bots.server_manager.buttons

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.interactions.components.Button

interface IMessageButton {
    fun getName(): String
    fun getButtonType(): IButtonType
    fun onButtonClicked(event: ButtonClickEvent)
    fun setButton(button: Button)
    fun cancel()

    enum class IButtonType {
        PRIMARY, SUCCESS, DANGER, DISABLED
    }
}