package org.abdevs.advanceddiscordlogger.api;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import org.abdevs.advanceddiscordlogger.enities.WebhookMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

public class WebhookMessageSender {
    /**
     * @param builder    WebhookMessage to be sent
     * @param username   Username of webhook message
     * @param avatarUrl  Avatar url of webhook message
     * @param webhookUrl Webhook url
     * @return true if success
     */
    public static boolean sendWebhookMessage(@NotNull WebhookMessage builder, @Nullable String username, @Nullable String avatarUrl, @NotNull String webhookUrl) {
        final WebhookEmbedBuilder webhookEmbedBuilder = new WebhookEmbedBuilder();
        final WebhookMessageBuilder webhookMessageBuilder = new WebhookMessageBuilder();

        username = username != null && username.equals("") ? null : username;
        webhookMessageBuilder.setUsername(username);

        avatarUrl = avatarUrl != null && avatarUrl.equals("") ? null : avatarUrl;
        webhookMessageBuilder.setAvatarUrl(avatarUrl);

        final String content = builder.getContent() != null && builder.getContent().equals("") ? null : builder.getContent();
        webhookMessageBuilder.setContent(content);

        final String embedTitle = builder.getEmbedTitle() != null && builder.getEmbedTitle().equals("") ? null : builder.getEmbedTitle();
        final String embedTitleUrl = builder.getEmbedTitleUrl() != null && builder.getEmbedTitleUrl().equals("") ? null : builder.getEmbedTitleUrl();
        WebhookEmbed.EmbedTitle embedTitleBuilder = null;

        if (embedTitle != null)
            embedTitleBuilder = new WebhookEmbed.EmbedTitle(embedTitle, embedTitleUrl);
        webhookEmbedBuilder.setTitle(embedTitleBuilder);

        final String embedAuthor = builder.getEmbedAuthor() != null && builder.getEmbedAuthor().equals("") ? null : builder.getEmbedAuthor();
        final String embedAuthorUrl = builder.getEmbedAuthorUrl() != null && builder.getEmbedAuthorUrl().equals("") ? null : builder.getEmbedAuthorUrl();
        final String embedAuthorIconUrl = builder.getEmbedAuthorIconUrl() != null && builder.getEmbedAuthorIconUrl().equals("") ? null : builder.getEmbedAuthorIconUrl();
        WebhookEmbed.EmbedAuthor embedAuthorBuilder = null;

        if (embedAuthor != null)
            embedAuthorBuilder = new WebhookEmbed.EmbedAuthor(embedAuthor, embedAuthorUrl, embedAuthorIconUrl);
        webhookEmbedBuilder.setAuthor(embedAuthorBuilder);

        final String description = builder.getDescription() != null && builder.getDescription().equals("") ? null : builder.getDescription();
        webhookEmbedBuilder.setDescription(description);

        if (builder.getFields() != null)
            builder.getFields().forEach(webhookEmbedBuilder::addField);

        final int color = builder.getColor();
        webhookEmbedBuilder.setColor(color);

        final String embedFooter = builder.getEmbedFooter() != null && builder.getEmbedFooter().equals("") ? null : builder.getEmbedFooter();
        final String embedFooterIconUrl = builder.getEmbedFooterIconUrl() != null && builder.getEmbedFooterIconUrl().equals("") ? null : builder.getEmbedFooterIconUrl();
        WebhookEmbed.EmbedFooter embedFooterBuilder = null;

        if (embedFooter != null)
            embedFooterBuilder = new WebhookEmbed.EmbedFooter(embedFooter, embedFooterIconUrl);
        webhookEmbedBuilder.setFooter(embedFooterBuilder);

        final boolean timestamp = builder.isTimestamp();
        if (timestamp)
            webhookEmbedBuilder.setTimestamp(Instant.now());

        final String thumbnailUrl = builder.getThumbnailUrl() != null && builder.getThumbnailUrl().equals("") ? null : builder.getThumbnailUrl();
        webhookEmbedBuilder.setThumbnailUrl(thumbnailUrl);

        final String imageUrl = builder.getImageUrl() != null && builder.getImageUrl().equals("") ? null : builder.getImageUrl();
        webhookEmbedBuilder.setImageUrl(imageUrl);

        if (!webhookEmbedBuilder.isEmpty()) webhookMessageBuilder.addEmbeds(webhookEmbedBuilder.build());

        if (!webhookMessageBuilder.isEmpty()) {
            try (final WebhookClient webhookClient = WebhookClient.withUrl(webhookUrl)) {
                webhookClient.send(webhookMessageBuilder.build());
                return true;
            }
        }
        return false;
    }
}
