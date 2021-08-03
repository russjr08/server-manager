package dev.omnicron.bots.server_manager.util

fun debug(contents: String) {
    println("[DEBUG][ServerManager] $contents")
}

fun error(contents: String) {
    println("[ERROR][ServerManager] $contents")
}

fun error(exception: Exception) {
    println("[ERROR][ServerManager] An error occurred, the details are as follows:\n${exception.localizedMessage}")
}