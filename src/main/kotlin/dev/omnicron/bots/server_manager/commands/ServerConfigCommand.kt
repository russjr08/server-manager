package dev.omnicron.bots.server_manager.commands

import com.jagrosh.jdautilities.command.SlashCommand
import dev.omnicron.bots.server_manager.models.ServerConfig
import dev.omnicron.bots.server_manager.util.ServerConfigUtils
import dev.omnicron.bots.server_manager.util.debug
import kotlinx.serialization.ExperimentalSerializationApi
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class ServerConfigCommand: SlashCommand() {

    init {
        this.name = "serverconfig"
        this.help = "Updates Server Manager's global configuration for this server"
        this.userPermissions = listOf(Permission.MANAGE_SERVER).toTypedArray()
        this.guildOnly = true
        this.children = arrayOf(SetPanelUrl(), SetGlobalApiToken(), SetAuthorizedRoles(), GetSavedServerConfig())
    }

    // Intentionally left blank since this is a sub-command
    override fun execute(event: SlashCommandEvent?) {}

    class SetPanelUrl: SlashCommand() {

        init {
            this.name = "set-panel-url"
            this.help = "Sets the URL to your server's Pterodactyl panel"
            this.userPermissions = listOf(Permission.MANAGE_SERVER).toTypedArray()

            this.options = arrayOf(OptionData(OptionType.STRING, "url",
                "The full URL to your server's panel").setRequired(true)).toMutableList()
        }

        @ExperimentalSerializationApi
        override fun execute(event: SlashCommandEvent?) {
            if(event != null) {
                // This option is set to required, it'll never be null
                var url = event.getOption("url")!!.asString

                if(url.endsWith("/")) {
                    url = url.dropLast(1)
                }

                event.reply("Setting the Panel URL for this server to $url").setEphemeral(true).queue()

                val configUtils = ServerConfigUtils()
                val config = configUtils.getConfigForServer(event.guild!!.id)
                config.apiUrl = url
                configUtils.saveConfigForServer(event.guild!!.id, config)
            }
        }

    }

    class SetGlobalApiToken: SlashCommand() {

        init {
            this.name = "set-global-key"
            this.help = "Sets the API Key that is used by default on this server"
            this.userPermissions = listOf(Permission.MANAGE_SERVER).toTypedArray()

            this.options = arrayOf(OptionData(OptionType.STRING, "key",
                "The API Key you wish to set").setRequired(true)).toMutableList()
        }

        @ExperimentalSerializationApi
        override fun execute(event: SlashCommandEvent?) {
            if(event != null) {
                val key = event.getOption("key")!!.asString

                event.reply("Setting the default API Key for this server to $key").setEphemeral(true).queue()

                val configUtils = ServerConfigUtils()
                val config = configUtils.getConfigForServer(event.guild!!.id)
                config.defaultApiToken = key
                configUtils.saveConfigForServer(event.guild!!.id, config)
            }
        }

    }

    class SetAuthorizedRoles: SlashCommand() {
        init {
            this.name = "set-roles"
            this.help = "Sets the roles that I will recognize as Administrators and Moderators for this server"
            this.userPermissions = listOf(Permission.MANAGE_SERVER).toTypedArray()

            this.options = mutableListOf(
                OptionData(OptionType.ROLE, "admin-role",
                "The Role that will be recognized as Administrators").setRequired(false),
                OptionData(OptionType.ROLE, "mod-role",
                    "The Role that will be recognized as Moderators").setRequired(false)
            )
        }

        @ExperimentalSerializationApi
        override fun execute(event: SlashCommandEvent?) {
            if(event != null) {

                val configUtils = ServerConfigUtils()
                val config = configUtils.getConfigForServer(event.guild!!.id)

                if(event.getOption("admin-role") != null) {
                    config.adminRoleId = event.getOption("admin-role")!!.asRole.id
                }

                if(event.getOption("mod-role") != null) {
                    config.modRoleId = event.getOption("mod-role")!!.asRole.id
                }

                configUtils.saveConfigForServer(event.guild!!.id, config)

                event.reply("Your requested updates have been set!").setEphemeral(true).queue()
            }
        }
    }

    class GetSavedServerConfig: SlashCommand() {
        init {
            this.name = "get-config"
            this.help = "Gets the config that is saved for this server"
            this.userPermissions = listOf(Permission.MANAGE_SERVER).toTypedArray()
        }

        // TODO: Add a prettier way to display "No Data"
        override fun execute(event: SlashCommandEvent?) {
            if(event != null) {
                val configUtils = ServerConfigUtils()
                val config = configUtils.getConfigForServer(event.guild!!.id)

                val embed = EmbedBuilder()
                    .setTitle("Server Config for ${event.guild!!.name}")
                    .setDescription("Your current server config is as follows:")

                embed.addField("Panel URL", config.apiUrl, false)
                embed.addField("Panel API Token", config.defaultApiToken, false)
                embed.addField("Administrator Role", "<@&${config.adminRoleId}>", true)
                embed.addField("Moderator Role", "<@&${config.modRoleId}>", true)

                event.replyEmbeds(embed.build()).setEphemeral(true).queue()
            }
        }
    }

}