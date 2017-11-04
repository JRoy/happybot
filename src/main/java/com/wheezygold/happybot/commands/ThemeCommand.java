package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Roles;
import com.wheezygold.happybot.util.Theme;
import com.wheezygold.happybot.util.ThemeManager;

import java.util.HashMap;

public class ThemeCommand extends Command {

    ThemeManager themeManager;

    public ThemeCommand(ThemeManager themeManager) {
        this.name = "theme";
        this.help = "Sets the theme of the discord.";
        StringBuilder sb = new StringBuilder();
        for (HashMap.Entry<String, HashMap<String, String>> curEntry : themeManager.getThemeData().entrySet()) {
            sb.append(curEntry.getKey()).append("/");
        }

        this.arguments = "<"+ sb.toString() + ">";
        this.guildOnly = false;
        this.category = new Category("Bot Management");
        this.themeManager = themeManager;
    }

    @Override
    protected void execute(CommandEvent e) {
        if (C.hasRole(e.getMember(), Roles.DEVELOPER)) {
            if (e.getArgs().isEmpty()) {
                e.replyError("**Correct Usage:** ^" + name + " " + arguments);
                return;
            }
//            if (themeManager.getThemeData().containsKey(e.getArgs().toLowerCase())) {
//
//            }
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
            } else if (e.getArgs().equals("winter")) {
                C.writeFile("theme.yml", "winter");
                C.log("Switched to Winter Theme!");
                e.replySuccess(":gear: Switching over to Winter Theme!");
                Theme.toWinter();
            } else {
                e.replyError("**Correct Usage:** ^" + name + " " + arguments);
            }
        } else {
            e.replyError(C.permMsg(Roles.DEVELOPER));
        }
    }
}
