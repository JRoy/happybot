package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Roles;
import com.wheezygold.happybot.util.Theme;

public class ThemeCommand extends Command {
    public ThemeCommand() {
        this.name = "theme";
        this.help = "Sets the theme of the discord.";
        this.arguments = "<normal/spooky>";
        this.guildOnly = false;
        this.category = new Category("Bot Management");
    }

    @Override
    protected void execute(CommandEvent e) {
        if(C.hasRole(e.getGuild(), e.getMember(), Roles.DEVELOPER)) {
            if (e.getArgs().isEmpty()) {
                e.replyError("**Correct Usage:** ^" + name + " " + arguments);
                return;
            }
            if (e.getArgs().equals("normal")) {
                C.writeFile("theme.yml", "normal");
                C.log("Switched to Normal Theme!");
                e.replySuccess(":gear: Switching over to Normal Theme!");
                Theme.toNormal();
            } else if (e.getArgs().equals("spooky")) {
                C.writeFile("theme.yml", "spooky");
                C.log("Switched to Spooky Theme!");
                e.replySuccess(":gear: Switching over to Spooky Theme!");
                Theme.toSpooky();
            } else {
                e.replyError("**Correct Usage:** ^" + name + " " + arguments);
            }
        } else {
            e.replyError(C.permMsg(Roles.DEVELOPER));
        }
    }
}
