package io.github.jroy.happybot.events;

import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Channels;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.util.Objects;

public class FanartPinner extends ListenerAdapter {

  @Override
  public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
    if (event.getMessage().getType().equals(MessageType.CHANNEL_PINNED_ADD) && event.getChannel().getId().equals(Channels.FANART.getId())) {
      event.getChannel().retrievePinnedMessages().queue(messages -> {
        Message message = messages.get(0);
        EmbedBuilder embed = new EmbedBuilder()
            .setTimestamp(OffsetDateTime.now())
            .setTitle(C.getFullName(message.getAuthor()), message.getJumpUrl())
            .setDescription(message.getContentRaw())
            .setColor(Objects.requireNonNull(message.getMember()).getColor());
        if (C.containsImage(message)) {
          embed.setImage(C.getImage(message));
        }
        Channels.FANART_ARCHIVE.getChannel().sendMessage(embed.build()).queue();
      });
    }
  }

  @Override
  public void onGuildMessageUpdate(@NotNull GuildMessageUpdateEvent event) {

  }
}
