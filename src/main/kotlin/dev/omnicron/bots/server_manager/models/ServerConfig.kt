package dev.omnicron.bots.server_manager.models

import kotlinx.serialization.Serializable

@Serializable
data class ServerConfig(
    var apiUrl: String = "",
    var modRoleId: String = "",
    var adminRoleId: String = "",
    var defaultApiToken: String = "",
    var userTokens: Map<String, String> = HashMap()
)
