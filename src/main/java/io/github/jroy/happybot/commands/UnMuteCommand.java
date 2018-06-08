package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.sql.timed.EventManager;
import io.github.jroy.happybot.sql.timed.EventType;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.core.entities.Member;

import java.sql.SQLException;

public class UnMuteCommand extends CommandBase {

    private EventManager eventManager;

    public UnMuteCommand(EventManager eventManager) {
        super("unmute", "<user>", "Un-mutes the target user", CommandCategory.STAFF, Roles.HELPER);
        this.eventManager = eventManager;
    }

    @Override
    protected void executeCommand(CommandEvent e) {
        if (!e.containsMention()) {
            e.replyError("Please mention a user to un-mute them!");
            return;
        }
        Member target = e.getMentionedMember();
        if (!eventManager.isPunished(target.getUser().getId(), EventType.MUTE)) {
            e.replyError("User is not muted!");
            return;
        }
        try {
            eventManager.deleteInfraction(target.getUser().getId(), EventType.MUTE);
            C.removeRole(target, Roles.MUTED);
            e.reply("User Un-muted!");
        } catch (SQLException e1) {
            e.replyError("Unable to delete infraction: " + e1.getMessage());
        }
    }
}
