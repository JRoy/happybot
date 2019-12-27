package io.github.jroy.happybot.events;

import io.github.jroy.happybot.sql.MessageFactory;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Channels;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class SubmitPinner extends ListenerAdapter {
  private final List<String> alreadyUsedMessages = new ArrayList<>();
  private final List<String> processingMessages = new ArrayList<>();

  @Override
  public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
    if (!event.getChannel().getId().equalsIgnoreCase(Channels.WELCOME_SUBMIT.getId())) {
      return;
    }

    Member member = event.getMember();
    Message message = event.getMessage();

    String content = event.getMessage().getContentDisplay().replaceFirst("\\((.*?)\\) ", "");
    String prefix = event.getMessage().getContentDisplay().replaceFirst(Pattern.quote(" " + content), "");
    prefix = prefix.replaceAll("([()])", "");
    prefix = prefix.replaceAll("[ ]", "_");

    MessageFactory.MessageType type = MessageFactory.MessageType.fromText(prefix);
    if (type == null) {
      message.delete().queue();
      C.privChannel(Objects.requireNonNull(member), "You specified an invalid message type! The valid ones are: " + MessageFactory.MessageType.getTypes(",") + "\n" +
          "The format for a message submission is:" + C.codeblock("(Message Type) Message Value") +
          "Note: Make sure to keep the message type inside of the parentheses");
      return;
    }

    switch (type) {
      case JOIN:
      case LEAVE: {
        if (content.startsWith("<user>") || content.startsWith("<player>")) {
          message.delete().queue();
          C.privChannel(Objects.requireNonNull(member), "Your message submission has been deleted: Do not begin join and leave messages with a <user>/<player>; that is automatically added.");
          return;
        }
        break;
      }
      case WARN: {
        if (!content.contains("<user>")) {
          message.delete().queue();
          C.privChannel(Objects.requireNonNull(member), "Your message submission has been deleted: Warning messages must include <user>!");
          return;
        }
        break;
      }
      case UPDATE_START:
      case UPDATE_END: {
        if (content.contains("<user>") || content.contains("<player>")) {
          message.delete().queue();
          C.privChannel(Objects.requireNonNull(member), "Your message submission has been deleted: Update messages do not support <user>/<player>.");
          return;
        }
        break;
      }
      case LEVEL: {
        if (!content.contains("<user>") || !content.contains("<level>")) {
          message.delete().queue();
          C.privChannel(Objects.requireNonNull(member), "Your message submission has been deleted: Leveling messages must include both the <user> and <level>!");
          return;
        }
        break;
      }
    }

    message.addReaction(Objects.requireNonNull(C.getGuild().getEmoteById("447793727532957716"))).complete();

  }

  @Override
  public void onGuildMessageUpdate(GuildMessageUpdateEvent event) {
    if (!event.getChannel().getId().equalsIgnoreCase(Channels.WELCOME_SUBMIT.getId())) {
      return;
    }

    if (!event.getMessage().isEdited()) {
      return;
    }

    if (processingMessages.contains(event.getMessage().getId())) {
      processingMessages.remove(event.getMessage().getId());
      return;
    }

    if (!C.hasRole(event.getMember(), Roles.SUPER_ADMIN)) {
      event.getMessage().delete().queue();
      C.privChannel(Objects.requireNonNull(event.getMember()), "You have attempted to edit a message in #message-submit! Your message has been deleted as you are not allowed to edit messages in this channel in order to prevent bamboozle.");
    }
  }

  @Override
  public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent e) {
    Message message;
    try {
      message = e.getChannel().retrieveMessageById(e.getMessageId()).complete();
    } catch (NullPointerException e1) {
      return;
    }
    if (e.getReactionEmote().getEmote().getId().equalsIgnoreCase("447793727532957716") && e.getChannel().getId().equalsIgnoreCase(Channels.WELCOME_SUBMIT.getId())) {
      int numberOfStars = message.getReactions().stream()
          .filter(reaction -> reaction.getReactionEmote().getId().equals("447793727532957716"))
          .findAny().map(MessageReaction::getCount).orElse(0);

      for (MessageReaction reaction : message.getReactions()) {
        if (!reaction.getReactionEmote().getId().equalsIgnoreCase("447793727532957716")) {
          continue;
        }
        for (User user : reaction.retrieveUsers().complete()) {
          if (user.isBot() || user.getId().equalsIgnoreCase(message.getAuthor().getId())) {
            numberOfStars--;
          }
        }
      }

      if (numberOfStars == 3 && !alreadyUsedMessages.contains(message.getId())) {
        processingMessages.add(message.getId());
        alreadyUsedMessages.add(message.getId());
        message.pin().queue();
        C.privChannel(Objects.requireNonNull(message.getMember()), "Your Message Submission has been pinned! The message should be added soon so congratulations!");
      }
    }
  }
}
