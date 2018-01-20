package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Roles;
import net.dv8tion.jda.core.entities.Member;

public class OgCommand extends Command {
    public OgCommand() {
        this.name = "og";
        this.help = "Gives/Takes a user's OG Role!";
        this.arguments = "<user>";
        this.guildOnly = true;
        this.category = new Category("Staff Tools");
    }

    @Override
    protected void execute(CommandEvent e) {
        if (C.hasRole(e.getMember(), Roles.ADMIN)) {
            if (e.getMessage().getMentionedUsers().size() == 1) {
                Member u = C.getMentionedMember(e);
                if (C.hasRole(u, Roles.OG)) {
                    C.removeRole(u, Roles.OG);
                    e.replySuccess(u.getUser().getAsMention() + " is no OG!");
                } else {
                    C.giveRole(u, Roles.OG);
                    e.replySuccess(u.getUser().getAsMention() + " has become OG!");
                }
            } else {
                e.replyError("**Correct Usage:** ^" + name + " " + arguments);
            }
        } else {
            e.replyError(C.permMsg(Roles.ADMIN));
        }
    }
}
