package io.github.jroy.happybot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.github.jroy.happybot.sql.timed.EventManager;
import io.github.jroy.happybot.sql.timed.EventType;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.core.entities.Member;

import java.sql.SQLException;

public class UnMuteCommand extends Command {

    private EventManager eventManager;

    public UnMuteCommand(EventManager eventManager) {
        this.name = "unmute";
        this.help = "Unmutes the target user.";
        this.arguments = "<user>";
        this.guildOnly = true;
        this.category = new Category("Staff Tools");
        this.eventManager = eventManager;
    }

    @Override
    protected void execute(CommandEvent e) {
        if (C.hasRole(e.getMember(), Roles.HELPER)) {
            if (!C.containsMention(e)) {
                e.replyError("Please mention a user to un-mute them!");
                return;
            }
            Member target = C.getMentionedMember(e);
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
        } else {
            e.replyError(C.permMsg(Roles.HELPER));
        }
    }
}
