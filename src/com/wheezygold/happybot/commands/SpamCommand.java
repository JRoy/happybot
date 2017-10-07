package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.Main;
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
        if (C.hasRole(e.getGuild(), e.getMember(), Roles.HELPER)) {
            if (e.getMessage().getMentionedUsers().size() == 1) {
                Member u = C.getMemberEvent(e);
                if (C.hasRole(e.getGuild(), u, Roles.EXP_SPAMMER)) {
                    e.getGuild().getController().removeSingleRoleFromMember(u, Roles.EXP_SPAMMER.getrole(e.getGuild())).reason("Role removed (by " + Main.getJda().getUserById(e.getMember().getUser().getId()).getName() + ") with ^expspammer").queue();
                    e.replySuccess(u.getUser().getAsMention() + " is no longer an EXP Spammer!");
                } else {
                    e.getGuild().getController().addSingleRoleToMember(u, Roles.EXP_SPAMMER.getrole(e.getGuild())).reason("Role added (by " + Main.getJda().getUserById(e.getMember().getUser().getId()).getName() + ") with ^expspammer").queue();
                    e.replySuccess(u.getUser().getAsMention() + " has become an EXP Spammer!");
                }
            } else {
                e.replyError("**Correct Usage:** ^" + name + " " + arguments);
            }
        } else {
            e.replyError(C.permMsg(Roles.HELPER));
        }
    }

}
