package dev.omnicron.bots.server_manager

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent

interface ReactionQueueItem<T, U> {
    fun run(event: MessageReactionAddEvent)
    fun isFor(): Message
    fun getAction(): ServerQueueAction<T, U>
    fun onCancelled();
}

interface ServerQueueAction<T, U> {
    fun performOnServer(): T;
    fun actingUpon(): U;
}