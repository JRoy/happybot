package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.events.star.StarMessages;
import io.github.jroy.happybot.util.Roles;
import org.apache.commons.lang3.StringUtils;

public class StarGoalCommand extends CommandBase {

  private StarMessages starMessages;

  public StarGoalCommand(StarMessages starMessages) {
    super("setgoal", "<message id> <goal (6-100)>", "Sets a custom star/heel goal for a message", CommandCategory.STAFF, Roles.MODERATOR);
    this.starMessages = starMessages;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (e.getArgs().isEmpty() || e.getSplitArgs().length < 2 || !StringUtils.isNumeric(e.getSplitArgs()[1]) || Integer.parseInt(e.getSplitArgs()[1]) < 6) {
      e.reply(invalid);
      return;
    }
    String messageId = e.getSplitArgs()[0];
    int goal = Integer.parseInt(e.getSplitArgs()[1]);

    if (messageId.length() != 18 || !StringUtils.isNumeric(messageId)) {
      e.reply("You have entered an invalid message id!");
      return;
    }

    if (starMessages.isAltered(messageId)) {
      e.reply("This message has already been altered, removing altercation!");
      starMessages.deleteStarAlter(messageId);
      return;
    }

    starMessages.addStarAlter(messageId, goal);
    e.reply("Message("+messageId+") will now only star at " + goal + " stars/heels!");
  }
}
