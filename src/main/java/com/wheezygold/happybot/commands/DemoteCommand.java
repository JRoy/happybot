package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Roles;
import net.dv8tion.jda.core.entities.Member;

public class DemoteCommand extends Command {

    public DemoteCommand() {
        this.name = "demote";
        this.help = "Demotes a user off the staff team.";
        this.arguments = "<user>";
        this.guildOnly = true;
        this.category = new Category("Staff Tools");
    }

    @Override
    protected void execute(CommandEvent e) {
        if (C.hasRole(e.getMember(), Roles.RECRUITER)) {
            if (e.getMessage().getMentionedUsers().size() == 1) {
                e.reply("Please wait while we look how to demote " + C.getMentionedMember(e).getAsMention() + "!");
                Member member = C.getMentionedMember(e);
                if (!C.hasRole(member, Roles.HELPER)) {
                    e.replyError("User is not on the staff team!");
                    return;
                }
                removeIfHasRole(member, Roles.SUPER_ADMIN);
                removeIfHasRole(member, Roles.ADMIN);
                removeIfHasRole(member, Roles.MODERATOR);
                removeIfHasRole(member, Roles.HELPER);
                e.replySuccess("User has been demoted!");
            } else {
                e.replyError("**Correct Usage:** ^" + name + " " + arguments);
            }
        } else {
            e.replyError(C.permMsg(Roles.RECRUITER));
        }
    }

    private void removeIfHasRole(Member member, Roles role) {
        if (C.hasRole(member, role)) C.removeRole(member, role);
    }
}
