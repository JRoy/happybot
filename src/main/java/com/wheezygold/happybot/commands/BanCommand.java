package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Roles;

public class BanCommand extends Command {
    public BanCommand() {
        this.name = "ban";
        this.help = "Bans target user from server.";
        this.arguments = "<user mention>";
        this.guildOnly = true;
        this.category = new Category("Staff Tools");
    }

    @Override
    protected void execute(CommandEvent e) {
        if (C.hasRole(e.getGuild(), e.getMember(), Roles.MODERATOR)) {
            if (e.getMessage().getMentionedUsers().size() == 1) {
                //String id = C.getMemberEvent(e).getUser().getId();
                String bname = C.getMemberEvent(e).getUser().getName();
                String bdescrim = C.getMemberEvent(e).getUser().getDiscriminator();
                C.getCtrl(e).ban(C.getMemberEvent(e).getUser(), 7, "Banned by Moderator: " + e.getMember().getUser().getName()).reason("Banned by Moderator: " + e.getMember().getUser().getName()).queue();
                e.replySuccess("User " + bname + "#" + bdescrim + " has been **FRIGGING BANNED** by " + e.getMember().getEffectiveName());
            } else {
                e.replyError("**Correct Usage:** ^" + name + " " + arguments);
            }
        } else {
            e.replyError(C.permMsg(Roles.MODERATOR));
        }
    }
}
