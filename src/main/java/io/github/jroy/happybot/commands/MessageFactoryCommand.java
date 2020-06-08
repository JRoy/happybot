package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.sql.MessageFactory;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Channels;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class MessageFactoryCommand extends CommandBase {

  private final MessageFactory messageFactory;
  private final Map<MessageFactory.MessageType, Map<Integer, Map<Integer, String>>> cachedTypes = new HashMap<>();

  public MessageFactoryCommand(MessageFactory messageFactory) {
    super("messages",
        "<add/delete/list/purge/pins> <type/id/type> <message/id>",
        "A command to deal with MessageFactory System.",
        CommandCategory.STAFF,
        Roles.SUPER_ADMIN);
    this.messageFactory = messageFactory;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (e.getSplitArgs().length < 1) {
      e.replyError(invalid);
      return;
    }

    if (e.getSplitArgs()[0].equalsIgnoreCase("list") && e.getSplitArgs().length > 2) {
      MessageFactory.MessageType type = MessageFactory.MessageType.fromText(e.getSplitArgs()[1]);
      if (type == null) {
        e.replyError("Invalid Type! Possible types are: " + MessageFactory.MessageType.getTypes(","));
        return;
      } else if (!StringUtils.isNumeric(e.getSplitArgs()[2])) {
        e.replyError("Invalid page number: " + e.getSplitArgs()[2]);
        return;
      }

      if (!cachedTypes.containsKey(type)) {
        try {
          cachedTypes.put(type, messageFactory.getPaginatedList(type));
        } catch (SQLException sqlException) {
          e.replyError("Error while fetching msgs: " + sqlException.getMessage());
          sqlException.printStackTrace();
          return;
        }
      }

      try {
        Map<Integer, Map<Integer, String>> pages = messageFactory.getPaginatedList(type);
        Integer pageNumber = Integer.valueOf(e.getSplitArgs()[2]);
        if (!pages.containsKey(pageNumber)) {
          e.replyError("Page number does not exist!");
          return;
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setFooter("Page " + pageNumber + "/" + pages.size());
        for (Map.Entry<Integer, String> entry : pages.get(pageNumber).entrySet()) {
          embed.addField("ID " + entry.getKey(), entry.getValue(), true);
        }
        e.reply(embed.build());
      } catch (SQLException sqlException) {
        e.replyError("Error while fetching messages: " + sqlException.getMessage());
        sqlException.printStackTrace();
      }
    } else if (e.getSplitArgs()[0].equalsIgnoreCase("purge")) {
      cachedTypes.clear();
      e.reply("purged");
    } else if (e.getSplitArgs()[0].equalsIgnoreCase("delete") && e.getSplitArgs().length > 1) {
      if (!StringUtils.isNumeric(e.getSplitArgs()[1])) {
        e.replyError("Invalid number: " + e.getSplitArgs()[1]);
        return;
      }

      try {
        messageFactory.deleteMessage(Integer.parseInt(e.getSplitArgs()[1]));
        e.reply("deleted");
      } catch (SQLException sqlException) {
        e.reply("shit hit fan");
        sqlException.printStackTrace();
      }
    } else if (e.getSplitArgs()[0].equalsIgnoreCase("add") && e.getSplitArgs().length > 2) {
      MessageFactory.MessageType type = MessageFactory.MessageType.fromText(e.getSplitArgs()[1]);
      if (type == null) {
        e.replyError("Invalid Type! Possible types are: " + MessageFactory.MessageType.getTypes(","));
        return;
      }
      String message = e.getArgs().replaceFirst(e.getSplitArgs()[0] + " " + e.getSplitArgs()[1] + " ", "");
      try {
        if (message.getBytes().length > 255) {
          e.replyError("Cannot exceed 255 bytes!");
          return;
        }
        messageFactory.addMessage(type, message);
        e.reply("Added message successfully!");
      } catch (SQLException e1) {
        e.replyError("Error while adding message: " + e1.getMessage());
        e1.printStackTrace();
      }
    } else if (e.getSplitArgs()[0].equalsIgnoreCase("pins")) {
      List<Message> pins = Channels.WELCOME_SUBMIT.getChannel().retrievePinnedMessages().complete();
      int added = 0;
      for (Message msg : pins) {
        String content = msg.getContentDisplay().replaceFirst("\\((.*?)\\) ", "");
        String prefix = msg.getContentDisplay().replaceFirst(Pattern.quote(" " + content), "");
        prefix = prefix.replaceAll("([()])", "");
        prefix = prefix.replaceAll("[ ]", "_");

        MessageFactory.MessageType type = MessageFactory.MessageType.fromText(prefix);
        if (type == null) {
          continue;
        }

        if (type == MessageFactory.MessageType.JOIN || type == MessageFactory.MessageType.LEAVE) {
          if (content.toLowerCase().startsWith("<player> ")) {
            content = content.replaceFirst("<player> ", "");
          } else if (content.toLowerCase().startsWith("<user> ")) {
            content = content.replaceFirst("<user> ", "");
          }
        }

        try {
          if (content.getBytes().length > 255) {
            e.replyError("One message by " + C.getFullName(msg.getAuthor()) + " exceeded 255 bytes!");
            msg.unpin().queue();
            continue;
          }
          messageFactory.addMessage(type, content);
          msg.unpin().queue();
          added++;
        } catch (SQLException e1) {
          e.replyError("Error while adding message: " + e1.getMessage());
        }
      }
      e.replySuccess("Processed " + added + " messages!");
    } else {
      e.replyError(invalid);
    }
  }
}
