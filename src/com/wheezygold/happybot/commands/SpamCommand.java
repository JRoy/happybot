package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.Main;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Roles;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

public class SpamCommand extends Command {

    private Member u;


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
                for (Role r : e.getGuild().getRoles()) {
                    if (r.getName().equalsIgnoreCase("EXP Spammer")) {
                        for (User m : e.getMessage().getMentionedUsers()) {
                            u = e.getGuild().getMember(m);
                        }
                        if (C.hasRole(e.getGuild(), u, Roles.EXP_SPAMMER)) {
                            e.getGuild().getController().removeSingleRoleFromMember(u, r).reason("Role removed (by " + Main.getJda().getUserById(e.getMember().getUser().getId()).getName() + ") with ^expspammer").queue();
                            e.replySuccess(u.getUser().getAsMention() + " is no longer an EXP Spammer!");
                        } else {
                            e.getGuild().getController().addSingleRoleToMember(u, r).reason("Role added (by " + Main.getJda().getUserById(e.getMember().getUser().getId()).getName() + ") with ^expspammer").queue();
                            e.replySuccess(u.getUser().getAsMention() + " has become an EXP Spammer!");
                        }
                    }
                }
            } else {
                e.replyError("Please (only) mention one user!");
            }
        } else {
            e.replyError(C.permMsg(Roles.HELPER));
        }
    }

}
