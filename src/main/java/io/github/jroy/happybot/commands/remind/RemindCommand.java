package io.github.jroy.happybot.commands.remind;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.sql.timed.EventManager;
import io.github.jroy.happybot.util.C;
import sh.okx.timeapi.api.TimeAPI;

import java.sql.SQLException;

public class RemindCommand extends CommandBase {

  private final EventManager eventManager;

  public RemindCommand(EventManager eventManager) {
    super("remind", "<time to remind you> <reason>", "Reminds you to do something at some time.", CommandCategory.FUN);
    this.eventManager = eventManager;
    this.aliases = new String[]{"remindme", "reminder"};
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (e.getSplitArgs().length >= 2) {
      TimeAPI wait;
      try {
        wait = new TimeAPI(e.getSplitArgs()[0]);
      } catch (IllegalArgumentException e1) {
        e.reply(invalid);
        return;
      }
      String reason = e.getArgs().replaceFirst(e.getSplitArgs()[0], "").trim();

      try {
        eventManager.createReminder(e.getMember().getUser().getId(), (long) wait.getMilliseconds(), reason);
        e.reply("I'll remind you to " + C.code(reason) + "!");
      } catch (SQLException e1) {
        e.replyError("I was unable to process your request!");
      }

    } else {
      e.replyError(invalid);
    }
  }
}
