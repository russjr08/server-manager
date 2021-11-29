package dev.omnicron.bots.server_manager.util

enum class ActionTypeResult(var message: String) {
    PENDING("\n\nResult: Pending"),
    CONFIRMED("\n\nResult: **Confirmed**"),
    FAILED("\n\nResult: **Expired**")
}