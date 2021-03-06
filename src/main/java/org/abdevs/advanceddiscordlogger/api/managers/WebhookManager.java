package org.abdevs.advanceddiscordlogger.api.managers;

import club.minnced.discord.webhook.external.JDAWebhookClient;
import org.abdevs.advanceddiscordlogger.enities.ExtensionData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface WebhookManager {
    /**
     * Build jda webhook client.
     *
     * @param webhookUrl the webhook url
     * @param extension  the extension
     * @return the jda webhook client
     */
    @NotNull
    JDAWebhookClient buildJDA(@NotNull String webhookUrl, @NotNull ExtensionData extension);

    /**
     * Gets {@link JDAWebhookClient} from {@param id}.
     *
     * @param id        the id
     * @param extension the extension
     * @return the client
     */
    @Nullable
    JDAWebhookClient getClient(long id, @NotNull ExtensionData extension);

    /**
     * Gets {@link JDAWebhookClient} from {@param webhookUrl}.
     *
     * @param webhookUrl the webhook url
     * @param extension  the extension
     * @return the client
     */
    @Nullable JDAWebhookClient getClient(@NotNull String webhookUrl, @NotNull ExtensionData extension);

    /**
     * Close {@link JDAWebhookClient} from {@param webhookUrl}.
     *
     * @param webhookUrl the webhook url
     * @param extension  the extension
     */
    void closeClient(@NotNull String webhookUrl, @NotNull ExtensionData extension);

    /**
     * Close {@link JDAWebhookClient} from {@param id}.
     *
     * @param id        the id
     * @param extension the extension
     */
    void closeClient(long id, @NotNull ExtensionData extension);

    /**
     * Close the provided {@param client}.
     *
     * @param client    the client
     * @param extension the extension
     */
    void closeClient(@NotNull JDAWebhookClient client, @NotNull ExtensionData extension);

    /**
     * Close all {@link JDAWebhookClient}.
     *
     * @param extension the extension
     */
    void closeAllClients(@NotNull ExtensionData extension);
}
