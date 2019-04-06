package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.Roles;
import org.apache.commons.lang3.StringUtils;

public class SlowmodeCommand extends CommandBase {
  public SlowmodeCommand() {
    super("slowmode", "<time in seconds>", "Enables/disables slowmode in the current channel channel", CommandCategory.STAFF, Roles.MODERATOR);
    this.aliases = new String[] { "slow" };
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (e.getArgs().isEmpty()) {
      if (e.getTextChannel().getSlowmode() > 0) {
        e.getTextChannel().getManager().setSlowmode(0).queue();
        e.replySuccess("Disabled Slowmode!");
        return;
      }
      e.replyError("Please specify a slowmode interval between 1-120 seconds!");
      return;
    }
    if (!StringUtils.isNumeric(e.getArgs())) {
      e.replyError("Slowmode interval must be a number!");
      return;
    }
    int sec = Integer.parseInt(e.getArgs());
    if (sec <= 0 || sec > 120) {
      e.replyError("Slowmode interval must be 1-120 seconds!");
      return;
    }

    e.getTextChannel().getManager().setSlowmode(sec).queue();
    e.reply("This channel' slowmode interval has been updated to " + sec + " seconds!");
  }
}
