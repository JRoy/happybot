package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;

public class RulesCommand extends CommandBase {

  public RulesCommand() {
    super("rules", null, "Links to the rules.", CommandCategory.GENERAL);
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    e.replySuccess("Here are the rules: <http://bit.ly/2ihUfAc>");
  }
}
