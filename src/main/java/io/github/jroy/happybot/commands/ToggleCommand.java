package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.commands.base.CommandFactory;
import io.github.jroy.happybot.util.Roles;

public class ToggleCommand extends CommandBase {

  private final CommandFactory commandFactory;

  public ToggleCommand(CommandFactory commandFactory) {
    super("toggle", "<command name>", "Toggles a command's disable state.", CommandCategory.BOT, Roles.DEVELOPER);
    this.commandFactory = commandFactory;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    String command = e.getArgs().replace("^", "");
    if (!commandFactory.getRegisteredCommands().containsKey(command)) {
      e.reply("This command does not exist!");
      return;
    }

    boolean disabled = commandFactory.getRegisteredCommands().get(command).isDisabled();
    commandFactory.getRegisteredCommands().get(command).setDisabled(!disabled);

    e.reply("Updated ^" + command + " Disabled State: " + !disabled);
  }
}
