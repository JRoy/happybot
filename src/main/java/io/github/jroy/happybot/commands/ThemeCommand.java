package io.github.jroy.happybot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.github.jroy.happybot.theme.ThemeManager;
import io.github.jroy.happybot.theme.exceptions.ThemeNotFoundException;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;

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
                try {
                    C.writeFile("theme.yml", e.getArgs());
                    themeManager.switchTheme(e.getArgs());
                    e.replySuccess(":gear: Switching over to " + themeManager.asToken(e.getArgs()).getName() + " Theme!");
                } catch (ThemeNotFoundException e1) {
                    e.replyError(":x: Error while switching themes: " + e1.getMessage());
                }
            } else {
                e.replyError("**Correct Usage:** ^" + name + " " + arguments);
            }
        } else {
            e.replyError(C.permMsg(Roles.DEVELOPER));
        }
    }
}
