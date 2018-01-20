package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Roles;
import net.dv8tion.jda.core.entities.Member;

public class PromoteCommand extends Command {

    public PromoteCommand() {
        this.name = "promote";
        this.help = "Promotes a user up the staff hierarchy.";
        this.arguments = "<user>";
        this.guildOnly = true;
        this.category = new Category("Staff Tools");
    }

    @Override
    protected void execute(CommandEvent e) {
        if (C.hasRole(e.getMember(), Roles.RECRUITER)) {
            if (e.getMessage().getMentionedUsers().size() == 1) {
                e.reply("Please wait while we look how to promote " + C.getMentionedMember(e).getAsMention() + "!");
                Member member = C.getMentionedMember(e);
                promoteMember(member, e);
            } else {
                e.replyError("**Correct Usage:** ^" + name + " " + arguments);
            }
        } else {
            e.replyError(C.permMsg(Roles.RECRUITER));
        }
    }

    private void promoteMember(Member member, CommandEvent event) {
        if (C.hasRole(member, Roles.SUPER_ADMIN)) {
            event.replySuccess("User is already the on highest level of promotion!");
        } else if (!promoteIfHasRole(Roles.ADMIN, Roles.SUPER_ADMIN, member, event)
                && !promoteIfHasRole(Roles.MODERATOR, Roles.ADMIN, member, event)
                && !promoteIfHasRole(Roles.HELPER, Roles.MODERATOR, member, event)
                && !promoteIfHasRole(Roles.FANS, Roles.HELPER, member, event)) {
            event.replyError("User has a malformed role!");
        }
    }

    /**
     * Promotes a member if they have the required role
     *
     * @return True if the member was promoted
     */
    private boolean promoteIfHasRole(Roles requiredRole, Roles promotionRole, Member member, CommandEvent event) {
        if (C.hasRole(member, requiredRole)) {
            event.replySuccess("User has been promoted to **" + promotionRole.getName() + "**!");
            event.getGuild().getController().addSingleRoleToMember(member, promotionRole.getRole()).reason("User Promotion!").queue();
            return true;
        }
        return false;
    }

}
