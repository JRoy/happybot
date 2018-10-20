package io.github.jroy.happybot.events;

import io.github.jroy.happybot.Main;
import io.github.jroy.happybot.sql.MessageFactory;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Channels;
import io.github.jroy.happybot.util.Constants;
import io.github.jroy.happybot.util.Emote;
import io.github.jroy.happybot.util.Roles;
import io.github.jroy.happybot.util.RuntimeEditor;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.events.StatusChangeEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class AutoMod extends ListenerAdapter {

  private final Pattern pattern;
  private List<Message> processedMessages = new ArrayList<>();
  private MessageFactory messageFactory;

  public AutoMod(MessageFactory messageFactory) {
    this.messageFactory = messageFactory;
    pattern = Pattern.compile("(?:https?://)?discord(?:app\\.com/invite|\\.gg)/(\\S+)", Pattern.CASE_INSENSITIVE);
  }

  //Update Notification Resolver
  @Override
  public void onStatusChange(StatusChangeEvent event) {
    if (event.getNewStatus() == JDA.Status.CONNECTED) {
      event.getJDA().getGuildById(Constants.GUILD_ID.get()).getTextChannelById(Channels.BOT_META.getId()).getHistory().retrievePast(10).queue(messages -> messages.forEach(message -> {
        message.getEmbeds().forEach(messageEmbed -> {
          if (messageEmbed != null && messageEmbed.getTitle().equalsIgnoreCase("Impending Update") && message.getAuthor() == Main.getJda().getUserById(Constants.BOT_ID.get()) && !message.isWebhookMessage()) {
            message.editMessage(new EmbedBuilder()
                .setTitle("Update Complete")
                .setDescription(messageFactory.getRawMessage(MessageFactory.MessageType.UPDATE_END) + "\nThis update has been finished in PID: " + ManagementFactory.getRuntimeMXBean().getName().split("[@]")[0])
                .build()).queue();
          }
        });
      }));
    }
  }

  /*
  Advert Filter
  Auto React
  Git Ping Handler
   */
  @Override
  public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
    Message message = event.getMessage();

    //Advert Checker
    if (RuntimeEditor.isFilteringAdverts() && checkForAdvertising(event.getMember(), message, event.getChannel())) {
      return;
    }

    //Auto React
    if (event.getChannel().getId().equals(Channels.UPDATES.getId()) || event.getChannel().getId().equals(Channels.STAFF_ANNOUNCEMENTS.getId())) {
      message.addReaction(Emote.getRandom().getEmote()).queue();
    }

    //Git Ping Handler
    if (message.getChannel() == Channels.BOT_META.getChannel() && message.isWebhookMessage()) {
      MessageEmbed embed = message.getEmbeds().get(0);
      if ((embed.getTitle().startsWith("[JRoy/happybot] Issue closed:") || embed.getTitle().startsWith("[JRoy/happybot] New comment on")) && !RuntimeEditor.isPingIssueClose()) {
        return;
      }
      Roles.GIT.getRole().getManager().setMentionable(true).complete();
      Channels.BOT_META.getChannel().sendMessage(Roles.GIT.getRole().getAsMention()).complete();
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      } finally {
        Roles.GIT.getRole().getManager().setMentionable(false).queue();
      }
    }

  }

  //Advert Filter
  @Override
  public void onGuildMessageUpdate(GuildMessageUpdateEvent event) {
    if (RuntimeEditor.isFilteringAdverts()) {
      checkForAdvertising(event.getMember(), event.getMessage(), event.getChannel());
    }
  }

  private boolean checkForAdvertising(Member member, Message message, TextChannel channel) {
    if (C.hasRole(member, Roles.SUPER_ADMIN) || C.hasRole(member, Roles.BOT)) {
      return false;
    }
    if (!pattern.matcher(message.getContentRaw()).find()) {
      return false;
    }
    message.delete().reason("Advertising Link with Message: " + message.getContentStripped()).complete();
    Channels.LOG.getChannel().sendMessage(member.getAsMention() + " attempted to advert the following link: " + message.getContentRaw()).queue();
    C.privChannel(member, "You cannot advertise in the happyheart guild!");
    if (!processedMessages.contains(message)) {
      channel.sendMessage(member.getAsMention() + "! Do not advert other discord servers!").queue();
      processedMessages.add(message);
    }
    return true;
  }

  //Teddy is hoe
  @Override
  public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
    if (event.getMember().getUser().getId().equals("194473148161327104") && event.getRoles().get(0).getId().equals(Roles.EXP_SPAMMER.getId())) {
      C.removeRole(event.getMember(), Roles.EXP_SPAMMER);
      Channels.RANDOM.getChannel().sendMessage("TEDDY YOU HOE").queue();
      Channels.RANDOM.getChannel().sendMessage("TEDDY YOU HOE").queue();
      Channels.RANDOM.getChannel().sendMessage("TEDDY YOU HOE").queue();
      Channels.RANDOM.getChannel().sendMessage("TEDDY YOU HOE").queue();
      Channels.RANDOM.getChannel().sendMessage("TEDDY YOU HOE").queue();
      Channels.RANDOM.getChannel().sendMessage("TEDDY YOU HOE").queue();
      Channels.RANDOM.getChannel().sendMessage("TEDDY YOU HOE").queue();
    }
  }

  @Override
  public void onShutdown(ShutdownEvent event) {
    System.exit(0);
  }
}
