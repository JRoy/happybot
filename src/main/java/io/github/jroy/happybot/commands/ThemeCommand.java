package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.theme.DiscordThemerImpl;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;
import io.github.wheezygold7931.discordthemer.exceptions.ThemeNotFoundException;

public class ThemeCommand extends CommandBase {

  private DiscordThemerImpl themeManager;

  public ThemeCommand(DiscordThemerImpl themeManager) {
    super("theme", "<Theme Name>", "Sets the theme of the discord server.", CommandCategory.BOT, Roles.SUPER_ADMIN);
    this.themeManager = themeManager;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (e.getArgs().isEmpty()) {
      e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
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
  }
}
