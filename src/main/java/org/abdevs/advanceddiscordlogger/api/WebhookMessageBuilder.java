package org.abdevs.advanceddiscordlogger.api;

import club.minnced.discord.webhook.send.WebhookEmbed;
import org.abdevs.advanceddiscordlogger.enities.WebhookMessage;

import java.util.List;

public class WebhookMessageBuilder {
    private String content;
    private String embedAuthor;
    private String embedAuthorUrl;
    private String embedAuthorIconUrl;
    private String embedTitle;
    private String embedTitleUrl;
    private String description;
    private List<WebhookEmbed.EmbedField> fields;
    private String embedFooter;
    private String embedFooterIconUrl;
    private int color;
    private boolean timestamp;
    private String thumbnailUrl;
    private String imageUrl;

    /**
     * @param content Message content
     * @return instance of this builder
     */
    public WebhookMessageBuilder setContent(String content) {
        this.content = content;
        return this;
    }

    /**
     * @param embedAuthor Embed author
     * @return instance of this builder
     */
    public WebhookMessageBuilder setEmbedAuthor(String embedAuthor) {
        this.embedAuthor = embedAuthor;
        return this;
    }

    /**
     * @param embedAuthorUrl Embed author url
     * @return instance of this builder
     */
    public WebhookMessageBuilder setEmbedAuthorUrl(String embedAuthorUrl) {
        this.embedAuthorUrl = embedAuthorUrl;
        return this;
    }

    /**
     * @param embedAuthorIconUrl Embed author icon url
     * @return instance of this builder
     */
    public WebhookMessageBuilder setEmbedAuthorIconUrl(String embedAuthorIconUrl) {
        this.embedAuthorIconUrl = embedAuthorIconUrl;
        return this;
    }

    /**
     * @param embedTitle Embed title
     * @return instance of this builder
     */
    public WebhookMessageBuilder setEmbedTitle(String embedTitle) {
        this.embedTitle = embedTitle;
        return this;
    }

    /**
     * @param embedTitleUrl Embed title url
     * @return instance of this builder
     */
    public WebhookMessageBuilder setEmbedTitleUrl(String embedTitleUrl) {
        this.embedTitleUrl = embedTitleUrl;
        return this;
    }

    /**
     * @param description Embed description
     * @return instance of this builder
     */
    public WebhookMessageBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * @param fields Embed fields
     * @return instance of this builder
     */
    public WebhookMessageBuilder setFields(List<WebhookEmbed.EmbedField> fields) {
        this.fields = fields;
        return this;
    }

    /**
     * @param embedFooter Embed footer
     * @return instance of this builder
     */
    public WebhookMessageBuilder setEmbedFooter(String embedFooter) {
        this.embedFooter = embedFooter;
        return this;
    }

    /**
     * @param embedFooterIconUrl Embed footer icon url
     * @return instance of this builder
     */
    public WebhookMessageBuilder setEmbedFooterIconUrl(String embedFooterIconUrl) {
        this.embedFooterIconUrl = embedFooterIconUrl;
        return this;
    }

    /**
     * @param color Embed color
     * @return instance of this builder
     */
    public WebhookMessageBuilder setColor(int color) {
        this.color = color;
        return this;
    }

    /**
     * @param timestamp Add timestamp with embed
     * @return instance of this builder
     */
    public WebhookMessageBuilder setTimestamp(boolean timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    /**
     * @param thumbnailUrl Embed thumbnail url
     * @return instance of this builder
     */
    public WebhookMessageBuilder setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
        return this;
    }

    /**
     * @param imageUrl Embed image url
     * @return instance of this builder
     */
    public WebhookMessageBuilder setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    /**
     * @return true if the embed is empty
     */
    public boolean isEmpty() {
        return (content == null || content.length() == 0) &&
                embedTitle == null &&
                thumbnailUrl == null &&
                embedAuthor == null &&
                embedFooter == null &&
                imageUrl == null &&
                color == 0 &&
                (description == null || description.length() == 0) &&
                (fields == null || fields.isEmpty());
    }

    /**
     * @return WebhookMessage build with this instance
     */
    public WebhookMessage build() {
        if (isEmpty())
            throw new IllegalStateException("Failed to send the message. Reason: WebhookMessageBuilder is empty");
        return new WebhookMessage(content, embedAuthor, embedAuthorUrl, embedAuthorIconUrl, embedTitle, embedTitleUrl, description, fields, embedFooter, embedFooterIconUrl, color, timestamp, thumbnailUrl, imageUrl);
    }
}
