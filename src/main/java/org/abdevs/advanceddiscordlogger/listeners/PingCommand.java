package org.abdevs.advanceddiscordlogger.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.temporal.ChronoUnit;

public class PingCommand extends ListenerAdapter {

    private final FileConfiguration config;

    public PingCommand(FileConfiguration config) {
        this.config = config;
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.isWebhookMessage() || event.getAuthor().isBot()) return;
        final Message message = event.getMessage();
        final String contentRaw = message.getContentRaw();
        final String command = config.getString("api.JDA.ping-command.command");
        final TextChannel channel = event.getChannel();
        if (!contentRaw.equals(command)) return;
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription("Pinging...").setColor(Color.GREEN).setFooter("Advanced Discord Logger");
        channel.sendMessage(embedBuilder.build()).queue(reqMessage -> {
            final long ping = message.getTimeCreated().until(reqMessage.getTimeCreated(), ChronoUnit.MILLIS);
            embedBuilder.setDescription("**Ping:** `" + ping + "ms`\n**WebSocket:** `" + message.getJDA().getGatewayPing() + "ms`");
            reqMessage.editMessage(embedBuilder.build()).queue();
        });
    }
}
