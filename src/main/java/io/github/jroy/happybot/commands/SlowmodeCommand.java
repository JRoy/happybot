package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.core.entities.impl.TextChannelImpl;
import org.apache.commons.lang3.StringUtils;

public class SlowmodeCommand extends CommandBase {
  public SlowmodeCommand() {
    super("slowmode", "<time in seconds>", "Enables/disables slowmode in the current channel channel", CommandCategory.STAFF, Roles.MODERATOR);
    this.aliases = new String[]{"slow"};
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (e.getTextChannel().getSlowmode() != 0 && e.getArgs().isEmpty()) {
      e.replyError("Slowmode has been disabled!");
      return;
    }
    if (!StringUtils.isNumeric(e.getArgs())) {
      e.replyError("Slowmode must be a number!");
      return;
    }
    int sec = Integer.parseInt(e.getArgs());
    if (sec <= 0 || sec > 120) {
      e.replyError("Slowmode delay must be 1-120 seconds!");
      return;
    }
    TextChannelImpl channel = (TextChannelImpl) e.getTextChannel();
    channel.setSlowmode(sec);
    e.reply("Channel Slowmode has been updated!");
  }
}
