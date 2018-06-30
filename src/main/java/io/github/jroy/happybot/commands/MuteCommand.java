package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.sql.timed.EventManager;
import io.github.jroy.happybot.sql.timed.EventType;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.core.entities.Member;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class MuteCommand extends CommandBase {

    private EventManager eventManager;

    public MuteCommand(EventManager eventManager) {
        super("mute", "<user> [<time in hours> <reason>]", "Toggles the mute of a user.", CommandCategory.STAFF, Roles.HELPER);
        this.eventManager = eventManager;
    }

    @Override
    protected void executeCommand(CommandEvent e) {
        String[] args = e.getSplitArgs();
        if (C.containsMention(e)) {
            e.replyError("Correct Usage: ^" + name + " " + arguments);
            return;
        }

        Member target = C.getMentionedMember(e);
        if (eventManager.isPunished(target.getUser().getId(), EventType.MUTE)) {
            try {
                eventManager.deleteInfraction(target.getUser().getId(), EventType.MUTE);
                C.removeRole(target, Roles.MUTED);
                e.reply("User un-muted!");
            } catch (SQLException e1) {
                e.replyError("Unable to delete infraction: " + e1.getMessage());
            }
        } else if(args.length >= 3 && StringUtils.isNumeric(args[1])) {
            long wait = TimeUnit.HOURS.toMillis(Integer.parseInt(args[1]));
            String reason = e.getArgs().replaceFirst("<(.*?)>", "").replaceFirst(" " + args[1] + " ", "");

            try {
                eventManager.createInfraction(target.getUser().getId(), wait, EventType.MUTE);
                C.giveRole(target, Roles.MUTED);
                e.replySuccess("User muted!");
                C.privChannel(target, "You have been muted for " + args[0] + " hours with reason: " + reason + "!");
            } catch (SQLException e1) {
                e.replyError("Could not mute user: " + e1.getMessage());
            }
        } else {
            e.replyError("Correct Usage: ^" + name + " " + arguments);
        }
    }
}
