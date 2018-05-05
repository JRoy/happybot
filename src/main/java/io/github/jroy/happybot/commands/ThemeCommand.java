package io.github.jroy.happybot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.github.jroy.happybot.theme.DiscordThemerImpl;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;
import io.github.wheezygold7931.discordthemer.exceptions.ThemeNotFoundException;

public class ThemeCommand extends Command {

    private DiscordThemerImpl themeManager;

    public ThemeCommand(DiscordThemerImpl themeManager) {
        this.name = "theme";
        this.help = "Sets the theme of the discord.";
        this.arguments = "<Theme Name>";
        this.guildOnly = false;
        this.category = new Category("Bot Management");
        this.themeManager = themeManager;
    }

    @Override
    protected void execute(CommandEvent e) {
        if (C.hasRole(e.getMember(), Roles.SUPER_ADMIN)) {
            if (e.getArgs().isEmpty()) {
                e.replyError("**Correct Usage:** ^" + name + " " + arguments);
                return;
            }
            if (themeManager.isValidTheme(e.getArgs())) {
                try {
                    themeManager.switchToTheme(e.getArgs());
                    e.replySuccess(":gear: Switched Theme!");
                } catch (ThemeNotFoundException e1) {
                    e.replyError(":x: Error while switching themes: " + e1.getMessage());
                }
            } else {
                e.replyError(":x: Invalid Theme!");
            }
        } else {
            e.replyError(C.permMsg(Roles.DEVELOPER));
        }
    }
}
