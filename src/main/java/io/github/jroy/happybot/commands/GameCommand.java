package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;

public class GameCommand extends CommandBase {

  public GameCommand() {
    super("games", "<create/start/list/join/spectate>", "Helper command for all things related to games.", CommandCategory.FUN);
  }

  @Override
  protected void executeCommand(CommandEvent event) {

  }
}
