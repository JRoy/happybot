package io.github.jroy.happybot.commands.warn;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.github.jroy.happybot.sql.WarningManager;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Channels;
import io.github.jroy.happybot.util.MessageFactory;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;

import java.awt.*;
import java.sql.SQLException;

public class WarnCommand extends Command {

    private WarningManager warningManager;
    private MessageFactory messageFactory;

    public WarnCommand(WarningManager warningManager, MessageFactory messageFactory) {
        this.name = "warn";
        this.arguments = "<user> <reason>";
        this.help = "Warn the target user.";
        this.guildOnly = true;
        this.category = new Category("Staff Tools");
        this.aliases = new String[]{"warning"};
        this.warningManager = warningManager;
        this.messageFactory = messageFactory;
    }

    @Override
    protected void execute(CommandEvent e) {
        if (C.hasRole(e.getMember(), Roles.HELPER)) {
            if (!C.containsMention(e)) {
                e.replyError("**Correct Usage:** ^" + name + " **<user>** <reason>");
                return;
            }
            if (e.getArgs().replaceAll("<(.*?)>", "").isEmpty()) {
                e.replyError("**Correct Usage:** ^" + name + " <user> **<reason>**");
                return;
            }

            String reason = e.getArgs().replaceFirst("<(.*?)> ", "");
            Member target = C.getMentionedMember(e);

            if (reason.contains("^rules")) {
                e.replyError("Please do not mention to review the rules, the bot does that for us now!");
                return;
            }

            try {
                int warnId = warningManager.spawnWarning(target.getUser().getId(), e.getMember().getUser().getId(), reason);
                C.privChannel(target, "You have been warned for: " + C.bold(reason) + "! To review the rules please type `^rules` in the random channel.");
                e.reply(messageFactory.getRawMessage(MessageFactory.MessageType.WARN).replaceAll("<player>", "**"+ target.getAsMention() + "**").replaceAll("<user>", "**"+ target.getAsMention() + "**"));
                Channels.LOG.getChannel().sendMessage(new EmbedBuilder()
                        .setAuthor(e.getMember().getUser().getName() + "#" + e.getMember().getUser().getDiscriminator(), null,  e.getMember().getUser().getAvatarUrl())
                        .setColor(Color.YELLOW)
                        .setThumbnail(target.getUser().getAvatarUrl())
                        .setDescription(":information_source: **Warning Created**\n" + "⚠ " + C.bold("Warned " + target.getUser().getName() + "#" + target.getUser().getDiscriminator()) + "\n:page_facing_up: " + C.bold("Reason: ") + reason + "\n:id: **Warn ID** " + String.valueOf(warnId))
                        .build()).queue();
            } catch (SQLException e1) {
                e.replyError("Oof Error: " + e1.getMessage());
            }
        } else {
            e.replyError(C.permMsg(Roles.HELPER));
        }
    }
}
