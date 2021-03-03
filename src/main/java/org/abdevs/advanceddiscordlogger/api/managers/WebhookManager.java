package org.abdevs.advanceddiscordlogger.api.managers;

import club.minnced.discord.webhook.external.JDAWebhookClient;
import org.abdevs.advanceddiscordlogger.enities.ExtensionData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface WebhookManager {
    @NotNull
    JDAWebhookClient buildJDA(@NotNull String webhookUrl, @NotNull ExtensionData extension);

    @Nullable
    JDAWebhookClient getClient(long id, @NotNull ExtensionData extension);

    @Nullable JDAWebhookClient getClient(@NotNull String webhookUrl, @NotNull ExtensionData extension);

    void closeClient(@NotNull String webhookUrl, @NotNull ExtensionData extension);

    void closeClient(long id, @NotNull ExtensionData extension);

    void closeClient(@NotNull JDAWebhookClient client, @NotNull ExtensionData extension);

    void closeAllClients(@NotNull ExtensionData extension);
}
