package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.sql.MessageFactory;
import io.github.jroy.happybot.util.Channels;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.core.entities.Message;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class MessageFactoryCommand extends CommandBase {

    private MessageFactory messageFactory;

    public MessageFactoryCommand(MessageFactory messageFactory) {
        super("messages",
                "<add/pins> <type> <message>",
                "A command to deal with MessageFactory System.",
                CommandCategory.STAFF,
                Roles.SUPER_ADMIN);
        this.messageFactory = messageFactory;
    }

    @Override
    protected void executeCommand(CommandEvent e) {
        if (e.getSplitArgs().length < 1) {
            e.replyError(invalid());
            return;
        }

        if (e.getSplitArgs()[0].equalsIgnoreCase("add") && e.getSplitArgs().length > 2) {
            MessageFactory.MessageType type = MessageFactory.MessageType.fromText(e.getSplitArgs()[1]);
            if (type == null) {
                e.replyError("Invalid Type! Possible types are: " + MessageFactory.MessageType.getTypes(","));
                return;
            }
            String message = e.getArgs().replaceFirst(e.getSplitArgs()[0] + " " + e.getSplitArgs()[1] + " ", "");
            try {
                messageFactory.addMessage(type, message);
                e.reply("Added message successfully!");
            } catch (SQLException e1) {
                e.replyError("Error while adding message: " + e1.getMessage());
                e1.printStackTrace();
            }
        } else if (e.getSplitArgs()[0].equalsIgnoreCase("pins")) {
            List<Message> pins = Channels.WELCOME_SUBMIT.getChannel().getPinnedMessages().complete();
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
                    messageFactory.addMessage(type, content);
                    msg.unpin().queue();
                    added++;
                } catch (SQLException e1) {
                    e.replyError("Error while adding message: " + e1.getMessage());
                }
            }
            e.replySuccess("Processed " + added + " messages!");
        } else {
            e.replyError(invalid());
        }
    }
}
