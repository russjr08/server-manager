package dev.omnicron.bots.server_manager

import dev.omnicron.bots.server_manager.util.ActionTypeResult
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import java.awt.Color

class Helpers {

    companion object Helpers {
        fun checkArguments(count: Int, message: Message, args: List<String>): Boolean {
            if(args.size < count) {
                val embed = EmbedBuilder()
                    .setTitle("Invalid amount of arguments provided")
                    .setDescription("The command you attempted to run requires more arguments!")
                    .setColor(Color.RED)
                    .setFooter(getFooterContent(), getLogoLink())
                    .build()

                message.channel.sendMessageEmbeds(embed).queue()
                return false
            }
            return true
        }

        fun getFooterContent(): String {
            var contents: String = if(Helpers::class.java.`package`.implementationVersion != null) {
                "Server Manager | Version ${Helpers::class.java.`package`.implementationVersion} | Made by Russ"
            } else {
                "Server Manager | DEVELOPMENT BUILD | Made by Russ"
            }
            return contents
        }

        fun sendInvalidPermissionsEmbed(message: Message) {
            val embed = EmbedBuilder()
                .setTitle("❌ Invalid Permissions!")
                .setDescription("You do not have permission to run this command!")
                .setColor(Color.RED)
                .setFooter(getFooterContent(), getLogoLink())
                .build()
            message.addReaction("❌").queue()
            message.channel.sendMessageEmbeds(embed).queue()

        }

        fun sendServersNotFoundEmbed(message: Message) {
            val embed = EmbedBuilder()
                .setTitle("❌ Server Not Found!")
                .setDescription("Pterodactyl reports no matching server for this request.")
                .setColor(Color.RED)
                .setFooter(getFooterContent(), getLogoLink())
                .build()
            message.addReaction("❌").queue()
            message.channel.sendMessageEmbeds(embed).queue()
        }

        fun sendTooManyServersMatchedEmbed(message: Message) {
            val embed = EmbedBuilder()
                .setTitle("\uD83E\uDD14 Too Many Results!")
                .setDescription("Multiple servers match this request, could you please be more specific.")
                .setColor(Color.RED)
                .setFooter(getFooterContent(), getLogoLink())
                .build()
            message.addReaction("❌").queue()
            message.channel.sendMessageEmbeds(embed).queue()
        }

        fun getActionConfirmationEmbed(actingUpon: String, actionType: String, result: ActionTypeResult,
                                       requiresMultiple: Boolean = false): MessageEmbed {
            val descriptionBuilder = StringBuilder()
            descriptionBuilder.append("Running this command requires confirmation, " +
                    "please react to my reaction to confirm within 20 seconds.\n\n")

            val color = when(result) {
                ActionTypeResult.PENDING -> Color.YELLOW
                ActionTypeResult.CONFIRMED -> Color.GREEN
                ActionTypeResult.FAILED -> Color.RED
            }

            if (requiresMultiple) {
                descriptionBuilder.append(
                    "Note: Moderators - this requires at least two moderators to confirm. " +
                            "An Administrator can also react to override the need for multiple confirmations.\n\n"
                )
            }

            descriptionBuilder.append(
                "Requested Server: $actingUpon\n" +
                        "Requested Action: **$actionType**" +
                        result.message
            )

            return EmbedBuilder()
                .setTitle("Confirm This Action")
                .setColor(color)
                .setDescription(descriptionBuilder.toString())
                .setFooter(dev.omnicron.bots.server_manager.Helpers.getFooterContent(), dev.omnicron.bots.server_manager.Helpers.getLogoLink())
                .build()
        }

        fun getLogoLink(): String = "https://i.russ.network/static/server-manager-logo.png"
    }

}