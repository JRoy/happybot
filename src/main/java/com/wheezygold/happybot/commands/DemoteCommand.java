package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Roles;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

public class DemoteCommand extends Command {

    private Member u;
    private Guild g;

    public DemoteCommand() {
        this.name = "demote";
        this.help = "Demotes a user off the staff team.";
        this.arguments = "<user>";
        this.guildOnly = true;
        this.category = new Category("Staff Tools");
    }

    @Override
    protected void execute(CommandEvent e) {
        if (C.hasRole(e.getGuild(), e.getMember(), Roles.RECRUITER)) {
            if (e.getMessage().getMentionedUsers().size() == 1) {
                e.reply("Please wait while we look how to demote " + C.getMemberEvent(e).getAsMention() + "!");
                u = C.getMemberEvent(e);
                g = C.getGuild();
                if (!C.hasRole(g, u, Roles.HELPER)) {
                    e.replyError("User is not on the staff team!");
                    return;
                }
                if (C.hasRole(g, u, Roles.SUPER_ADMIN)) {
                    C.removeRole(u, Roles.SUPER_ADMIN);
                }
                if (C.hasRole(g, u, Roles.ADMIN)) {
                    C.removeRole(u, Roles.ADMIN);
                }
                if (C.hasRole(g, u, Roles.MODERATOR)) {
                    C.removeRole(u, Roles.MODERATOR);
                }
                if (C.hasRole(g, u, Roles.HELPER)) {
                    C.removeRole(u, Roles.HELPER);
                }
                e.replySuccess("User has been demoted!");
            } else {
                e.replyError("**Correct Usage:** ^" + name + " " + arguments);
            }
        } else {
            e.replyError(C.permMsg(Roles.RECRUITER));
        }
    }
}
