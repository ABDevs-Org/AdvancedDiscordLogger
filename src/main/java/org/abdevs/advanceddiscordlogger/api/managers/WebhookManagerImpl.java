package org.abdevs.advanceddiscordlogger.api.managers;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.external.JDAWebhookClient;
import org.abdevs.advanceddiscordlogger.AdvancedDiscordLogger;
import org.abdevs.advanceddiscordlogger.enities.ExtensionData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;

public class WebhookManagerImpl implements WebhookManager {

    @Override
    @NotNull
    public JDAWebhookClient buildJDA(@NotNull String webhookUrl, @NotNull ExtensionData extension) {
        final JDAWebhookClient jdaWebhookClient = new WebhookClientBuilder(webhookUrl).buildJDA();
        final ConcurrentHashMap<ExtensionData, List<JDAWebhookClient>> extensionWebhooks = AdvancedDiscordLogger.getPlugin().getExtensionWebhooks();
        if (!extensionWebhooks.containsKey(extension)) {
            final ArrayList<JDAWebhookClient> clients = new ArrayList<>();
            clients.add(jdaWebhookClient);
            extensionWebhooks.put(extension, clients);
        } else extensionWebhooks.get(extension).add(jdaWebhookClient);
        return jdaWebhookClient;
    }

    @Override
    @Nullable
    public JDAWebhookClient getClient(long id, @NotNull ExtensionData extension) {
        final List<JDAWebhookClient> jdaWebhookClients = AdvancedDiscordLogger.getPlugin().getExtensionWebhooks().get(extension);
        return jdaWebhookClients.stream().filter(client -> client.getId() == id).findFirst().orElse(null);
    }

    @Override
    @Nullable
    public JDAWebhookClient getClient(@NotNull String webhookUrl, @NotNull ExtensionData extension) {
        final Matcher matcher = WebhookClientBuilder.WEBHOOK_PATTERN.matcher(webhookUrl);
        return getClient(Long.parseUnsignedLong(matcher.group(1)), extension);
    }

    @Override
    public void closeClient(@NotNull String webhookUrl, @NotNull ExtensionData extension) {
        final List<JDAWebhookClient> clients = AdvancedDiscordLogger.getPlugin().getExtensionWebhooks().get(extension);
        clients.stream().filter(fClient -> fClient.getUrl().equals(webhookUrl)).findFirst().ifPresent(client -> closeClient(client, extension));
    }

    @Override
    public void closeClient(long id, @NotNull ExtensionData extension) {
        final List<JDAWebhookClient> clients = AdvancedDiscordLogger.getPlugin().getExtensionWebhooks().get(extension);
        clients.stream().filter(fClient -> fClient.getId() == id).findFirst().ifPresent(client -> closeClient(client, extension));
    }

    @Override
    public void closeClient(@NotNull JDAWebhookClient client, @NotNull ExtensionData extension) {
        final List<JDAWebhookClient> clients = AdvancedDiscordLogger.getPlugin().getExtensionWebhooks().get(extension);
        if (clients.contains(client)) {
            if (!client.isShutdown()) client.close();
            clients.remove(client);
        }
    }

    @Override
    public void closeAllClients(@NotNull ExtensionData extension) {
        final List<JDAWebhookClient> clients = AdvancedDiscordLogger.getPlugin().getExtensionWebhooks().get(extension);
        clients.forEach(client -> closeClient(client, extension));
    }
}
