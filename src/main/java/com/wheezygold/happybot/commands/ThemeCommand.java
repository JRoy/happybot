package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.Main;
import com.wheezygold.happybot.theme.ThemeManager;
import com.wheezygold.happybot.theme.exceptions.ThemeNotFoundException;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Roles;

public class ThemeCommand extends Command {

    private ThemeManager themeManager;

    public ThemeCommand(ThemeManager themeManager) {
        this.name = "theme";
        this.help = "Sets the theme of the discord.";
        StringBuilder sb = new StringBuilder();

        for (Object curEntry : themeManager.getThemes().toArray()) {
            sb.append(curEntry).append("/");
        }

        this.arguments = "<"+ sb.toString().replaceAll("/$", "") + ">";
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
            if (themeManager.getThemes().contains(e.getArgs())) {
                if (!themeManager.isBusyTransferring()) {
                    try {
                        C.writeFile("theme.yml", e.getArgs());
                        themeManager.switchTheme(e.getArgs());
                        Main.updateTheme();
                        e.replySuccess(":gear: Switching over to " + themeManager.asToken(e.getArgs()).getName() + " Theme!");
                    } catch (ThemeNotFoundException e1) {
                        e.replyError(":x: Error while switching themes: " + e1.getMessage());
                    }
                } else {
                    e.replyError(":x: The theme system is currently busy!");
                }
            } else {
                e.replyError("**Correct Usage:** ^" + name + " " + arguments);
            }
        } else {
            e.replyError(C.permMsg(Roles.DEVELOPER));
        }
    }
}
