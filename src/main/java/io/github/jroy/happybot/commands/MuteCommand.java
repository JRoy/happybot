package io.github.jroy.happybot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.github.jroy.happybot.sql.timed.EventManager;
import io.github.jroy.happybot.sql.timed.EventType;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.core.entities.Member;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class MuteCommand extends Command {

    private EventManager eventManager;

    public MuteCommand(EventManager eventManager) {
        this.name = "mute";
        this.help = "Mute target user for x hours";
        this.arguments = "<user> <time in hours> <reason>";
        this.guildOnly = true;
        this.category = new Category("Staff Tools");
        this.eventManager = eventManager;
    }

    @Override
    protected void execute(CommandEvent e) {
        if (C.hasRole(e.getMember(), Roles.HELPER)) {
            String[] args = e.getArgs().split("[ ]");
            if (args.length < 3 || !C.containsMention(e) || !StringUtils.isNumeric(args[1])) {
                e.replyError("Correct Usage: ^" + name + " " + arguments);
                return;
            }

            Member target = C.getMentionedMember(e);
            long wait = TimeUnit.HOURS.toMillis(Integer.parseInt(args[1]));
            String reason = e.getArgs().replaceFirst("<(.*?)>", "").replaceFirst(" " + args[1] + " ", "");

            try {
                eventManager.createInfraction(target.getUser().getId(), wait, EventType.MUTE);
                C.giveRole(target, Roles.MUTED);
                e.replySuccess("User Muted!");
                C.privChannel(target, "You have been muted for " + args[0] + " hours with reason: " + reason + "!");
            } catch (SQLException e1) {
                e.replyError("Could not mute user: " + e1.getMessage());
            }

        } else {
            e.replyError(C.permMsg(Roles.HELPER));
        }
    }
}
