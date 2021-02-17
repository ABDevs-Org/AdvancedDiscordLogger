package org.abdevs.advanceddiscordlogger.enities;

import club.minnced.discord.webhook.send.WebhookEmbed;

import java.util.List;

public class WebhookMessage {
    private final String embedAuthor;
    private final String embedAuthorUrl;
    private final String embedAuthorIconUrl;
    private final String embedTitle;
    private final String embedTitleUrl;
    private final String embedFooter;
    private final String embedFooterIconUrl;
    private final String content;
    private final String description;
    private final int color;
    private final String thumbnailUrl;
    private final String imageUrl;
    private final boolean timestamp;
    private final List<WebhookEmbed.EmbedField> fields;

    public WebhookMessage(String content, String embedAuthor, String embedAuthorUrl, String embedAuthorIconUrl, String embedTitle, String embedTitleUrl, String description, List<WebhookEmbed.EmbedField> fields, String embedFooter, String embedFooterIconUrl, int color, boolean timestamp, String thumbnailUrl, String imageUrl) {
        this.content = content;
        this.embedAuthor = embedAuthor;
        this.embedAuthorUrl = embedAuthorUrl;
        this.embedAuthorIconUrl = embedAuthorIconUrl;
        this.embedTitle = embedTitle;
        this.embedTitleUrl = embedTitleUrl;
        this.description = description;
        this.fields = fields;
        this.embedFooter = embedFooter;
        this.embedFooterIconUrl = embedFooterIconUrl;
        this.color = color;
        this.timestamp = timestamp;
        this.thumbnailUrl = thumbnailUrl;
        this.imageUrl = imageUrl;
    }

    public String getEmbedAuthor() {
        return embedAuthor;
    }

    public String getEmbedAuthorUrl() {
        return embedAuthorUrl;
    }

    public String getEmbedAuthorIconUrl() {
        return embedAuthorIconUrl;
    }

    public String getEmbedTitle() {
        return embedTitle;
    }

    public String getEmbedTitleUrl() {
        return embedTitleUrl;
    }

    public String getEmbedFooter() {
        return embedFooter;
    }

    public String getEmbedFooterIconUrl() {
        return embedFooterIconUrl;
    }

    public String getContent() {
        return content;
    }

    public String getDescription() {
        return description;
    }

    public int getColor() {
        return color;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isTimestamp() {
        return timestamp;
    }

    public List<WebhookEmbed.EmbedField> getFields() {
        return fields;
    }

}
