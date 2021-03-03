package org.abdevs.advanceddiscordlogger.enities;

public class LogInfoData {
    private final boolean isWebhook;
    private final String webhookUrl;
    private final String username;
    private final String avatarUrl;
    private final long channelId;

    public LogInfoData(boolean isWebhook, String webhookUrl, String username, String avatarUrl, long channelId) {
        this.isWebhook = isWebhook;
        this.webhookUrl = webhookUrl;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.channelId = channelId;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public long getChannelId() {
        return channelId;
    }

    public boolean isWebhook() {
        return isWebhook;
    }
}
