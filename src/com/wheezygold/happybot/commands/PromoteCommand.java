package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Roles;
import net.dv8tion.jda.core.entities.Member;

public class PromoteCommand extends Command{

    private Member u;


    public PromoteCommand() {
        this.name = "promote";
        this.help = "Gives/Takes a user's EXP Spammer Role!";
        this.arguments = "<user>";
        this.guildOnly = true;
        this.category = new Category("Staff Tools");
    }

    @Override
    protected void execute(CommandEvent e) {
        if (C.hasRole(e.getGuild(), e.getMember(), Roles.SUPER_ADMIN)) {
            if (e.getMessage().getMentionedUsers().size() == 1) {
                e.reply("Please wait while we look how promote " + C.getMemberEvent(e).getAsMention() + "!");
                u = C.getMemberEvent(e);
                if (C.hasRole(e.getGuild(), u, Roles.SUPER_ADMIN)) {
                    e.replySuccess("User is already the highest level of promotion!");
                } else if (C.hasRole(e.getGuild(), u, Roles.ADMIN)) {
                    e.replySuccess("User has been promoted to **Super Admin**!");
                    e.getGuild().getController().addSingleRoleToMember(u, Roles.SUPER_ADMIN.getrole(e.getGuild())).reason("User Promotion!").queue();
                } else if (C.hasRole(e.getGuild(), u, Roles.MODERATOR)) {
                    e.replySuccess("User has been promoted to **Admin**!");
                    e.getGuild().getController().addSingleRoleToMember(u, Roles.ADMIN.getrole(e.getGuild())).reason("User Promotion!").queue();
                } else if (C.hasRole(e.getGuild(), u, Roles.HELPER)) {
                    e.replySuccess("User has been promoted to **Moderator**!");
                    e.getGuild().getController().addSingleRoleToMember(u, Roles.MODERATOR.getrole(e.getGuild())).reason("User Promotion!").queue();
                } else if (C.hasRole(e.getGuild(), u, Roles.FANS)) {
                    e.replySuccess("User has been promoted to **Helper**!");
                    e.getGuild().getController().addSingleRoleToMember(u, Roles.HELPER.getrole(e.getGuild())).reason("User Promotion!").queue();
                } else {
                    e.replyError("User has a malformed role!");
                }
            } else {
                e.replyError("Please (only) mention one user!");
            }
        } else {
            e.replyError(C.permMsg(Roles.SUPER_ADMIN));
        }
    }

}
