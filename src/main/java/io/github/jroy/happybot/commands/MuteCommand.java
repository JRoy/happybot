package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.sql.timed.EventManager;
import io.github.jroy.happybot.sql.timed.EventType;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Channels;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import sh.okx.timeapi.api.TimeAPI;

import java.awt.*;
import java.sql.SQLException;

public class MuteCommand extends CommandBase {

    private EventManager eventManager;

    public MuteCommand(EventManager eventManager) {
        super("mute", "<user> [<time> <reason>]", "Toggles the mute of a user.", CommandCategory.STAFF, Roles.HELPER);
        this.eventManager = eventManager;
    }

    @Override
    protected void executeCommand(CommandEvent e) {
        String[] args = e.getSplitArgs();
        if (!C.containsMention(e)) {
            e.replyError("Correct Usage: ^" + name + " " + arguments);
            return;
        }

        Member target = C.getMentionedMember(e);
        if (eventManager.isPunished(target.getUser().getId(), EventType.MUTE)) {
            try {
                eventManager.deleteInfraction(target.getUser().getId(), EventType.MUTE);
                C.removeRole(target, Roles.MUTED);
                e.reply("User un-muted!");
                Channels.LOG.getChannel().sendMessage(new EmbedBuilder()
                        .setAuthor(C.getFullName(e.getMember().getUser()), null,  e.getMember().getUser().getAvatarUrl())
                        .setColor(Color.CYAN)
                        .setThumbnail(target.getUser().getAvatarUrl())
                        .setDescription(":information_source: **User Un-Muted**\n" + C.bold("Un-Muted " + target.getUser().getName() + "#" + target.getUser().getDiscriminator()))
                        .build()).queue();
            } catch (SQLException e1) {
                e.replyError("Unable to delete infraction: " + e1.getMessage());
            }
        } else if (args.length >= 3) {
            TimeAPI wait;
            try {
                wait = new TimeAPI(args[1]);
            } catch (IllegalArgumentException e1) {
                e.reply(invalid);
                return;
            }
//            long wait = TimeUnit.HOURS.toMillis(Integer.parseInt(args[1]));
            String reason = e.getArgs().replaceFirst("<(.*?)>", "").replaceFirst(" " + args[1] + " ", "");

            try {
                eventManager.createInfraction(target.getUser().getId(), (long) wait.getMilliseconds(), EventType.MUTE);
                C.giveRole(target, Roles.MUTED);
                e.replySuccess("User muted!");
                Channels.LOG.getChannel().sendMessage(new EmbedBuilder()
                        .setAuthor(C.getFullName(e.getMember().getUser()), null,  e.getMember().getUser().getAvatarUrl())
                        .setColor(Color.CYAN)
                        .setThumbnail(target.getUser().getAvatarUrl())
                        .setDescription(":information_source: **User Muted**\n" + C.bold("Muted " + target.getUser().getName() + "#" + target.getUser().getDiscriminator()) + "\n:page_facing_up: " + C.bold("Reason: ") + reason + "\n:timer: **Duration** " + wait.getHours() + " hours.")
                        .build()).queue();
                C.privChannel(target, "You have been muted for " + wait.getHours() + " hours with reason: " + reason + "!");
            } catch (SQLException e1) {
                e.replyError("Could not mute user: " + e1.getMessage());
            }
        } else {
            e.replyError(invalid);
        }
    }
}
