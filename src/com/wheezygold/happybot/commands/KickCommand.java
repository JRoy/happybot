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
    }

    @Override
    protected void execute(CommandEvent e) {
        if (C.hasRole(e.getGuild(), e.getMember(), Roles.MODERATOR)) {
            if (e.getMessage().getMentionedUsers().size() == 1) {
                String bname = C.getMemberEvent(e).getUser().getName();
                String bdescrim = C.getMemberEvent(e).getUser().getDiscriminator();
                C.getCtrl(e).kick(C.getMemberEvent(e)).reason("Banned by Moderator: " + e.getMember().getUser().getName()).queue();
                e.replySuccess("User " + bname + "#" + bdescrim + " has been **FLIPPIN KICKED** by " + e.getMember().getEffectiveName());
            } else {
                e.replyError("**Correct Usage:** ^" + name + " " + arguments);
            }
        } else {
            e.replyError(C.permMsg(Roles.MODERATOR));
        }
    }
}
