package io.github.jroy.happybot.events;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.jroy.happybot.Main;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Channels;
import io.github.jroy.happybot.util.Logger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.Webhook;
import net.dv8tion.jda.core.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberNickChangeEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.events.role.RoleCreateEvent;
import net.dv8tion.jda.core.events.role.RoleDeleteEvent;
import net.dv8tion.jda.core.events.role.update.RoleUpdateNameEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookMessageBuilder;

import java.awt.*;
import java.time.OffsetDateTime;

public class LoggingFactory extends ListenerAdapter {
  private static final String WEBHOOK_ID = "466642500153769984";
  private Webhook webhook = null;
  private final Cache<String, Message> cache = CacheBuilder.newBuilder()
      .maximumSize(100)
      .build();

  public LoggingFactory() {
    Logger.info("Loading Logger Factory...");
    for (Webhook curHook : Channels.LOG.getChannel().getWebhooks().complete()) {
      if (curHook.getId().equals(WEBHOOK_ID)) {
        webhook = curHook;
        Main.getJda().addEventListener(this);
        Logger.info("Loaded Logger Factory!");
        sendLogMessage(new EmbedBuilder().setTitle("Bot Status Change").setDescription("Discord Logger has started!").setColor(Color.GREEN).build());
        break;
      }
    }
    if (webhook == null) {
      Logger.error("Log Webhook Not Found! Logging will be disabled for this instance!");
    }
  }

  private void sendLogMessage(MessageEmbed embed) {
    WebhookClient client = webhook.newClient().build();
    WebhookMessageBuilder messageBuilder = new WebhookMessageBuilder()
        .addEmbeds(embed)
        .setUsername("happybot-logger")
        .setAvatarUrl(Main.getJda().getSelfUser().getAvatarUrl());
    client.send(messageBuilder.build());
    client.close();
  }

  @Override
  public void onGuildBan(GuildBanEvent e) {
    sendLogMessage(new EmbedBuilder()
        .setAuthor("Member Banned", null, e.getUser().getAvatarUrl())
        .setDescription(e.getUser().getAsMention() + " " + C.getFullName(e.getUser()))
        .setThumbnail(e.getUser().getAvatarUrl())
        .setColor(Color.RED)
        .setFooter("ID: " + e.getUser().getId(), null).build());
  }

  @Override
  public void onGuildUnban(GuildUnbanEvent e) {
    sendLogMessage(new EmbedBuilder()
        .setAuthor("Member Unbanned", null, e.getUser().getAvatarUrl())
        .setDescription(e.getUser().getAsMention() + " " + C.getFullName(e.getUser()))
        .setThumbnail(e.getUser().getAvatarUrl())
        .setColor(Color.CYAN)
        .setFooter("ID: " + e.getUser().getId(), null).build());
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
      sendLogMessage(new EmbedBuilder()
          .setAuthor(e.getGuild().getName(), null, e.getGuild().getIconUrl())
          .setDescription(C.bold("Message deleted in ") + e.getChannel().getAsMention())
          .setColor(Color.RED)
          .setTimestamp(OffsetDateTime.now())
          .setFooter("ID: " + e.getMessageId(), null).build());
      return;
    }
    cache.invalidate(e.getMessageId());

    String desc = "";

    if (!deleted.getContentRaw().isEmpty()) {
      desc = deleted.getContentRaw();
    }

    sendLogMessage(new EmbedBuilder()
        .setAuthor(C.getFullName(deleted.getAuthor()))
        .setDescription(C.bold("Message sent by ") + deleted.getAuthor().getAsMention() + C.bold(" deleted in ") + deleted.getTextChannel().getAsMention() + "\n" + desc)
        .setColor(Color.RED)
        .setTimestamp(OffsetDateTime.now())
        .setFooter("ID: " + e.getMessageId(), null).build());
  }

  @Override
  public void onTextChannelCreate(TextChannelCreateEvent e) {
    sendLogMessage(new EmbedBuilder()
        .setAuthor(e.getGuild().getName(), null, e.getGuild().getIconUrl())
        .setDescription("Channel Created: #" + e.getChannel().getName())
        .setColor(Color.GREEN)
        .setFooter("ID: " + e.getChannel().getId(), null).build());
  }

  @Override
  public void onTextChannelDelete(TextChannelDeleteEvent e) {
    sendLogMessage(new EmbedBuilder()
        .setAuthor(e.getGuild().getName(), null, e.getGuild().getIconUrl())
        .setDescription("Channel Deleted: #" + e.getChannel().getName())
        .setColor(Color.RED)
        .setFooter("ID: " + e.getChannel().getId(), null).build());
  }

  @Override
  public void onRoleCreate(RoleCreateEvent e) {
    sendLogMessage(new EmbedBuilder()
        .setAuthor(e.getGuild().getName(), null, e.getGuild().getIconUrl())
        .setDescription("Role Created: " + e.getRole().getName())
        .setColor(Color.GREEN)
        .setFooter("ID: " + e.getRole().getId(), null).build());
  }

  @Override
  public void onRoleDelete(RoleDeleteEvent e) {
    sendLogMessage(new EmbedBuilder()
        .setAuthor(e.getGuild().getName(), null, e.getGuild().getIconUrl())
        .setDescription("Role Deleted: " + e.getRole().getName())
        .setColor(Color.RED)
        .setFooter("ID: " + e.getRole().getId(), null).build());
  }

  @Override
  public void onRoleUpdateName(RoleUpdateNameEvent e) {
    sendLogMessage(new EmbedBuilder()
        .setAuthor(e.getGuild().getName(), null, e.getGuild().getIconUrl())
        .setDescription("Role Updated: " + e.getRole().getName())
        .addField("New Name", e.getNewName(), false)
        .addField("Old Name", e.getOldName(), false)
        .setColor(Color.CYAN)
        .setFooter("ID: " + e.getRole().getId(), null).build());
  }

  @Override
  public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent e) {
    sendLogMessage(new EmbedBuilder()
        .setAuthor(C.getFullName(e.getUser()), null, e.getUser().getAvatarUrl())
        .setDescription(e.getUser().getAsMention() + " " + C.bold("was given the ") + "`" + C.prettyRoleArray(e.getRoles()) + "` " + C.bold("role(s)!"))
        .setColor(Color.CYAN)
        .setFooter("ID: " + e.getUser().getId(), null).build());
  }

  @Override
  public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent e) {
    sendLogMessage(new EmbedBuilder()
        .setAuthor(C.getFullName(e.getUser()), null, e.getUser().getAvatarUrl())
        .setDescription(e.getUser().getAsMention() + " " + C.bold("was removed from the ") + "`" + C.prettyRoleArray(e.getRoles()) + "` " + C.bold("role(s)!"))
        .setColor(Color.CYAN)
        .setFooter("ID: " + e.getUser().getId(), null).build());
  }

  @Override
  public void onGuildMemberNickChange(GuildMemberNickChangeEvent e) {
    sendLogMessage(new EmbedBuilder()
        .setAuthor(C.getFullName(e.getUser()), null, e.getUser().getAvatarUrl())
        .setDescription(e.getUser().getAsMention() + " " + C.bold("nickname changed!"))
        .addField("New Nick", e.getNewNick() != null ? e.getNewNick() : "N/A", false)
        .addField("Old Nick", e.getPrevNick() != null ? e.getPrevNick() : "N/A", false)
        .setColor(Color.CYAN)
        .setFooter("ID: " + e.getUser().getId(), null).build());
  }
}
