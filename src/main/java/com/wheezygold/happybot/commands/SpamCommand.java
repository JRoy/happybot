package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Roles;
import net.dv8tion.jda.core.entities.Member;

public class SpamCommand extends Command {

    public SpamCommand() {
        this.name = "expspammer";
        this.help = "Gives/Takes a user's EXP Spammer Role!";
        this.arguments = "<user>";
        this.guildOnly = true;
        this.category = new Category("Staff Tools");
    }

    @Override
    protected void execute(CommandEvent e) {
        if (C.hasRole(e.getMember(), Roles.HELPER)) {
            if (e.getMessage().getMentionedUsers().size() == 1) {
                Member u = C.getMentionedMember(e);
                if (C.hasRole(u, Roles.EXP_SPAMMER)) {
                    C.removeRole(u, Roles.EXP_SPAMMER);
                    e.replySuccess(u.getUser().getAsMention() + " is no longer an EXP Spammer!");
                } else {
                    C.giveRole(u, Roles.EXP_SPAMMER);
                    e.replySuccess(u.getUser().getAsMention() + " has become an EXP Spammer!");
                    C.privChannel(C.getMentionedMember(e), "You have become an EXP Spammer! Please ask to get this removed 1 week from now!");
                }
            } else {
                e.replyError("**Correct Usage:** ^" + name + " " + arguments);
            }
        } else {
            e.replyError(C.permMsg(Roles.HELPER));
        }
    }

}
