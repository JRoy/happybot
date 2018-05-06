package io.github.jroy.happybot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.github.jroy.happybot.sql.timed.EventManager;
import io.github.jroy.happybot.sql.timed.EventType;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Constants;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.core.entities.Member;

import java.sql.SQLException;

public class SpamCommand extends Command {

    private EventManager eventManager;

    public SpamCommand(EventManager eventManager) {
        this.name = "expspammer";
        this.help = "Gives/Takes a user's EXP Spammer Role!";
        this.arguments = "<user>";
        this.guildOnly = true;
        this.category = new Category("Staff Tools");
        this.eventManager = eventManager;
    }

    @Override
    protected void execute(CommandEvent e) {
        if (C.hasRole(e.getMember(), Roles.HELPER)) {
            if (e.getMessage().getMentionedUsers().size() == 1) {
                Member u = C.getMentionedMember(e);
                if (C.hasRole(u, Roles.EXP_SPAMMER)) {
                    if (eventManager.isPunished(u.getUser().getId(), EventType.XP)) {
                        try {
                            eventManager.deleteInfraction(u.getUser().getId(), EventType.XP);
                        } catch (SQLException e1) {
                            e.replyError("Error while deleting infraction: " + e1.getMessage());
                            e1.printStackTrace();
                        }
                    }
                    C.removeRole(u, Roles.EXP_SPAMMER);
                    e.replySuccess(u.getUser().getAsMention() + " is no longer an EXP Spammer!");
                } else {
                    C.giveRole(u, Roles.EXP_SPAMMER);
                    e.replySuccess(u.getUser().getAsMention() + " has become an EXP Spammer!");
                    C.privChannel(C.getMentionedMember(e), "You have become an EXP Spammer! Please ask to get this removed 1 week from now!");
                    if (eventManager.isPunished(u.getUser().getId(), EventType.XP)) {
                        try {
                            eventManager.deleteInfraction(u.getUser().getId(), EventType.XP);
                        } catch (SQLException e1) {
                            e.replyError("Error while deleting old infractions: " + e1.getMessage());
                            e1.printStackTrace();
                        }
                    }
                    try {
                        eventManager.createInfraction(u.getUser().getId(), Long.parseLong(Constants.EXP_SPAMMER_TIME.get()), EventType.XP);
                    } catch (SQLException e1) {
                        e.replyError("Error while creating infraction: " + e1.getMessage());
                        e1.printStackTrace();
                    }
                }
            } else {
                e.replyError("**Correct Usage:** ^" + name + " " + arguments);
            }
        } else {
            e.replyError(C.permMsg(Roles.HELPER));
        }
    }

}
