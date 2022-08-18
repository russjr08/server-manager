package dev.omnicron.bots.server_manager.util

import dev.omnicron.bots.server_manager.models.ServerConfig
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

/**
 * This class handles the serializing and deserializing of a [ServerConfig] instance.
 *
 * Constructing this class will create the "/data/config" directory if it doesn't already exist.
 *
 * @see ServerConfig
 */
class ServerConfigUtils {

    private val configFolder = File("data/config")

    init {
        if(!configFolder.exists()) {
            debug("You don't have a config folder yet - I'll create that for you now!")
            configFolder.mkdir()
        }
    }


    /**
     * Retrieves the [ServerConfig] for the Discord Server with the specified ID from the disk.
     * If no configuration file already exists for the provided Server, a blank [ServerConfig] will be returned.
     *
     * Because a blank [ServerConfig] is returned if no config is found, it is always safe to call this function.
     *
     * @see ServerConfig
     * @param serverId The Discord Server ID ("Snowflake") to find a [ServerConfig] for
     */
    @ExperimentalSerializationApi
    fun getConfigForServer(serverId: String): ServerConfig {
        val configFile = File("data/config/${serverId}.json")

        if(configFile.exists()) {
            return Json.decodeFromStream<ServerConfig>(configFile.inputStream())
        }

        return ServerConfig()
    }

    /**
     * Persists the [ServerConfig] provided for the Discord Server with the specified ID to the disk.
     *
     * This function may throw an [IOException] if for some reason the writing failed. In this case, an
     * error should be shown to the invoker!
     *
     * @param serverId The Discord Server ID ("Snowflake") to persist the [ServerConfig] for
     * @param config The [ServerConfig] that will be persisted to the disk
     * @throws IOException Thrown if the persistence operation fails
     * @see ServerConfig
     */
    @Throws(IOException::class)
    fun saveConfigForServer(serverId: String, config: ServerConfig) {
        val configFilePath = Paths.get("data/config/${serverId}.json")
        try {
            Files.writeString(configFilePath, Json.encodeToString(config))
        } catch (exception: IOException) {
            error("Failed to save the config for server with ID: $serverId!")
            error(exception.localizedMessage)
            throw exception
        }
    }

}