package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.events.star.StarMessages;
import io.github.jroy.happybot.util.Roles;

public class StarGoalCommand extends CommandBase {

  public StarGoalCommand(StarMessages starMessages) {
    super("setgoal", "<message id> <goal (6-100)>", "Sets a custom star/heel goal for a message", CommandCategory.STAFF, Roles.MODERATOR);
  }

  @Override
  protected void executeCommand(CommandEvent event) {

  }
}
