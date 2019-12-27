package io.github.jroy.happybot.commands.remind;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.sql.timed.EventManager;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;

public class EditRemindCommand extends CommandBase {

  private final EventManager eventManager;

  public EditRemindCommand(EventManager eventManager) {
    super("editreminder", "<id> <new reason>", "Edit your reminder's reason.", CommandCategory.FUN);
    this.aliases = new String[]{"ereminder", "eremind", "editremind"};
    this.eventManager = eventManager;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (e.getSplitArgs().length >= 2 && StringUtils.isNumeric(e.getSplitArgs()[0])) {
      int id = Integer.parseInt(e.getSplitArgs()[0]);
      String reason = e.getArgs().replaceFirst(e.getSplitArgs()[0], "");

      if (eventManager.isInvalidIdPair(id, e.getMember().getUser().getId())) {
        e.reply("Either that is not your reminder or that reminder id doesn't exist");
        return;
      }

      try {
        eventManager.setReminderReason(id, reason);
        e.replySuccess("Updated reminder reason!");
      } catch (SQLException e1) {
        e.replyError("Error while setting your reminder reason!");
      }
    } else {
      e.reply(invalid);
    }
  }
}
