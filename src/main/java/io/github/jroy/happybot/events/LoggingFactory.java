package io.github.jroy.happybot.events;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.jroy.happybot.Main;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Channels;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.awt.*;
import java.time.OffsetDateTime;
import java.util.Objects;

@Slf4j
public class LoggingFactory extends ListenerAdapter {
  private static final String WEBHOOK_ID = "466642500153769984";
  private Webhook webhook = null;
  private final Cache<String, Message> cache = CacheBuilder.newBuilder()
      .maximumSize(100)
      .build();

  public LoggingFactory() {
    log.info("Loading Logger Factory...");
    for (Webhook curHook : Channels.LOG.getChannel().retrieveWebhooks().complete()) {
      if (curHook.getId().equals(WEBHOOK_ID)) {
        webhook = curHook;
        Main.getJda().addEventListener(this);
        log.info("Loaded Logger Factory!");
        sendLogMessage(new WebhookEmbedBuilder().setTitle(new WebhookEmbed.EmbedTitle("Bot Status Change", null)).setDescription("Discord Logger has started!").setColor(Color.GREEN.getRGB()).build());
        break;
      }
    }
    if (webhook == null) {
      log.error("Log Webhook Not Found! Logging will be disabled for this instance!");
    }
  }

  private void sendLogMessage(WebhookEmbed embed) {
    WebhookClient client = WebhookClient.withId(webhook.getIdLong(), Objects.requireNonNull(webhook.getToken()));
    WebhookMessageBuilder messageBuilder = new WebhookMessageBuilder()
        .addEmbeds(embed)
        .setUsername("happybot-logger")
        .setAvatarUrl(Main.getJda().getSelfUser().getAvatarUrl());
    client.send(messageBuilder.build());
    client.close();
  }

  @Override
  public void onGuildBan(GuildBanEvent e) {
    sendLogMessage(new WebhookEmbedBuilder()
        .setAuthor(new WebhookEmbed.EmbedAuthor("Member Banned", e.getUser().getAvatarUrl(), null))
        .setDescription(e.getUser().getAsMention() + " " + C.getFullName(e.getUser()))
        .setThumbnailUrl(e.getUser().getAvatarUrl())
        .setColor(Color.RED.getRGB())
        .setFooter(new WebhookEmbed.EmbedFooter("ID: " + e.getUser().getId(), null)).build());
  }

  @Override
  public void onGuildUnban(GuildUnbanEvent e) {
    sendLogMessage(new WebhookEmbedBuilder()
        .setAuthor(new WebhookEmbed.EmbedAuthor("Member Unbanned", e.getUser().getAvatarUrl(), null))
        .setDescription(e.getUser().getAsMention() + " " + C.getFullName(e.getUser()))
        .setThumbnailUrl(e.getUser().getAvatarUrl())
        .setColor(Color.CYAN.getRGB())
        .setFooter(new WebhookEmbed.EmbedFooter("ID: " + e.getUser().getId(), null)).build());
  }

  @Override
  public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
    cache.put(e.getMessage().getId(), e.getMessage());
  }

  @Override
  public void onGuildMessageUpdate(GuildMessageUpdateEvent e) {
    if (cache.asMap().containsKey(e.getMessage().getId())) {
      cache.put(e.getMessage().getId(), e.getMessage());
    }
  }

  @Override
  public void onGuildMessageDelete(GuildMessageDeleteEvent e) {
    if (e.getChannel().getId().equals(Channels.TRUE_FALSE_GAME.getId())) { //Ignore Mass-Spam
      return;
    }

    Message deleted = cache.getIfPresent(e.getMessageId());
    if (deleted == null) {
      sendLogMessage(new WebhookEmbedBuilder()
          .setAuthor(new WebhookEmbed.EmbedAuthor(e.getGuild().getName(), e.getGuild().getIconUrl(), null))
          .setDescription(C.bold("Message deleted in ") + e.getChannel().getAsMention())
          .setColor(Color.RED.getRGB())
          .setTimestamp(OffsetDateTime.now())
          .setFooter(new WebhookEmbed.EmbedFooter("ID: " + e.getMessageId(), null)).build());
      return;
    }
    cache.invalidate(e.getMessageId());

    String desc = "";

    if (!deleted.getContentRaw().isEmpty()) {
      desc = deleted.getContentRaw();
    }

    sendLogMessage(new WebhookEmbedBuilder()
        .setAuthor(new WebhookEmbed.EmbedAuthor(C.getFullName(deleted.getAuthor()), null, null))
        .setDescription(C.bold("Message sent by ") + deleted.getAuthor().getAsMention() + C.bold(" deleted in ") + deleted.getTextChannel().getAsMention() + "\n" + desc)
        .setColor(Color.RED.getRGB())
        .setTimestamp(OffsetDateTime.now())
        .setFooter(new WebhookEmbed.EmbedFooter("ID: " + e.getMessageId(), null)).build());
  }

  @Override
  public void onTextChannelCreate(TextChannelCreateEvent e) {
    sendLogMessage(new WebhookEmbedBuilder()
        .setAuthor(new WebhookEmbed.EmbedAuthor(e.getGuild().getName(), e.getGuild().getIconUrl(), null))
        .setDescription("Channel Created: #" + e.getChannel().getName())
        .setColor(Color.GREEN.getRGB())
        .setFooter(new WebhookEmbed.EmbedFooter("ID: " + e.getChannel().getId(), null)).build());
  }

  @Override
  public void onTextChannelDelete(TextChannelDeleteEvent e) {
    sendLogMessage(new WebhookEmbedBuilder()
        .setAuthor(new WebhookEmbed.EmbedAuthor(e.getGuild().getName(), e.getGuild().getIconUrl(), null))
        .setDescription("Channel Deleted: #" + e.getChannel().getName())
        .setColor(Color.RED.getRGB())
        .setFooter(new WebhookEmbed.EmbedFooter("ID: " + e.getChannel().getId(), null)).build());
  }

  @Override
  public void onRoleCreate(RoleCreateEvent e) {
    sendLogMessage(new WebhookEmbedBuilder()
        .setAuthor(new WebhookEmbed.EmbedAuthor(e.getGuild().getName(), e.getGuild().getIconUrl(), null))
        .setDescription("Role Created: " + e.getRole().getName())
        .setColor(Color.GREEN.getRGB())
        .setFooter(new WebhookEmbed.EmbedFooter("ID: " + e.getRole().getId(), null)).build());
  }

  @Override
  public void onRoleDelete(RoleDeleteEvent e) {
    sendLogMessage(new WebhookEmbedBuilder()
        .setAuthor(new WebhookEmbed.EmbedAuthor(e.getGuild().getName(), e.getGuild().getIconUrl(), null))
        .setDescription("Role Deleted: " + e.getRole().getName())
        .setColor(Color.RED.getRGB())
        .setFooter(new WebhookEmbed.EmbedFooter("ID: " + e.getRole().getId(), null)).build());
  }

  @Override
  public void onRoleUpdateName(RoleUpdateNameEvent e) {
    sendLogMessage(new WebhookEmbedBuilder()
        .setAuthor(new WebhookEmbed.EmbedAuthor(e.getGuild().getName(), e.getGuild().getIconUrl(), null))
        .setDescription("Role Updated: " + e.getRole().getName())
        .addField(new WebhookEmbed.EmbedField(false, "New Name", e.getNewName()))
        .addField(new WebhookEmbed.EmbedField(false, "Old Name", e.getOldName()))
        .setColor(Color.CYAN.getRGB())
        .setFooter(new WebhookEmbed.EmbedFooter("ID: " + e.getRole().getId(), null)).build());
  }

  @Override
  public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent e) {
    sendLogMessage(new WebhookEmbedBuilder()
        .setAuthor(new WebhookEmbed.EmbedAuthor(C.getFullName(e.getUser()), e.getUser().getAvatarUrl(), null))
        .setDescription(e.getUser().getAsMention() + " " + C.bold("was given the ") + "`" + C.prettyRoleArray(e.getRoles()) + "` " + C.bold("role(s)!"))
        .setColor(Color.CYAN.getRGB())
        .setFooter(new WebhookEmbed.EmbedFooter("ID: " + e.getUser().getId(), null)).build());
  }

  @Override
  public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent e) {
    sendLogMessage(new WebhookEmbedBuilder()
        .setAuthor(new WebhookEmbed.EmbedAuthor(C.getFullName(e.getUser()), e.getUser().getAvatarUrl(), null))
        .setDescription(e.getUser().getAsMention() + " " + C.bold("was removed from the ") + "`" + C.prettyRoleArray(e.getRoles()) + "` " + C.bold("role(s)!"))
        .setColor(Color.CYAN.getRGB())
        .setFooter(new WebhookEmbed.EmbedFooter("ID: " + e.getUser().getId(), null)).build());
  }

  @Override
  public void onGuildMemberUpdateNickname(@Nonnull GuildMemberUpdateNicknameEvent e) {
    sendLogMessage(new WebhookEmbedBuilder()
        .setAuthor(new WebhookEmbed.EmbedAuthor(C.getFullName(e.getUser()), e.getUser().getAvatarUrl(), null))
        .setDescription(e.getUser().getAsMention() + " " + C.bold("nickname changed!"))
        .addField(new WebhookEmbed.EmbedField(false, "New Nick", e.getNewNickname() != null ? e.getNewNickname() : "N/A"))
        .addField(new WebhookEmbed.EmbedField(false, "Old Nick", e.getOldNickname() != null ? e.getOldNickname() : "N/A"))
        .setColor(Color.CYAN.getRGB())
        .setFooter(new WebhookEmbed.EmbedFooter("ID: " + e.getUser().getId(), null)).build());
  }
}
