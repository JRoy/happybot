package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Roles;
import net.dv8tion.jda.core.entities.Member;

public class FansCommand extends Command {

    public FansCommand() {
        this.name = "fixfans";
        this.help = "Fixes broken users with no Fans Role";
        this.guildOnly = true;
        this.category = new Category("Staff Tools");
    }

    @Override
    protected void execute(CommandEvent e) {
        if (C.hasRole(e.getMember(), Roles.ADMIN)) {
            e.reply("Loading...");
            int affected = 0;
            for (Member curM : C.getGuild().getMembers()) {
                if (!curM.getUser().isBot()) {
                    if (!C.hasRole(curM, Roles.FANS)) {
                        C.giveRole(curM, Roles.FANS);
                        affected++;
                    }
                }
            }
            e.replySuccess("All Done!\n" + String.valueOf(affected) + " Users Affected!");
        } else {
            e.replyError(C.permMsg(Roles.ADMIN));
        }
    }
}
