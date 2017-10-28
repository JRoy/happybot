package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Roles;

public class KickCommand extends Command {
    public KickCommand() {
        this.name = "kick";
        this.help = "Kicks target user from server.";
        this.arguments = "<user mention>";
        this.guildOnly = true;
        this.category = new Category("Staff Tools");
        this.aliases = new String[]{"deport"};
    }

    @Override
    protected void execute(CommandEvent e) {
        if (C.hasRole(e.getMember(), Roles.HELPER)) {
            if (e.getMessage().getMentionedUsers().size() == 1) {
                String bname = C.getMentionedMember(e).getUser().getName();
                String bdescrim = C.getMentionedMember(e).getUser().getDiscriminator();
                C.getCtrl(e).kick(C.getMentionedMember(e)).reason("Banned by Moderator: " + e.getMember().getUser().getName()).queue();
                e.replySuccess("User " + bname + "#" + bdescrim + " has been **FLIPPIN KICKED** by " + e.getMember().getEffectiveName());
            } else {
                e.replyError("**Correct Usage:** ^" + name + " " + arguments);
            }
        } else {
            e.replyError(C.permMsg(Roles.HELPER));
        }
    }
}
