package io.github.jroy.happybot.commands.warn;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.sql.WarningManager;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;
import io.github.jroy.happybot.util.RuntimeEditor;
import org.apache.commons.lang3.StringUtils;

public class EditWarningCommand extends CommandBase {

  private WarningManager warningManager;

  public EditWarningCommand(WarningManager warningManager) {
    super("editwarn", "<warning ID> <new reason>", "Edits the target warning.", CommandCategory.STAFF, Roles.HELPER);
    this.aliases = new String[]{"editwarning", "ewarn"};
    this.warningManager = warningManager;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    String[] args = e.getSplitArgs();
    if (args.length < 1) {
      e.replyError(C.bold("Correct Usage:") + " ^" + name + " **<warning ID> <new reason>**");
      return;
    }
    if (!StringUtils.isNumeric(args[0])) {
      e.replyError(C.bold("Correct Usage:") + " ^" + name + " **<warning ID>** <new reason>");
      return;
    }
    int id = Integer.parseInt(args[0]);
    String newReason = e.getArgs().replaceFirst(args[0] + " ", "");
    if (!warningManager.isValidWarning(id)) {
      e.replyError(C.bold("Correct Usage:") + " ^" + name + " **<warning ID>** <new reason>");
      return;
    }
    if (!warningManager.getWarnAuthorId(id).equals(e.getMember().getUser().getId()) && !C.hasRole(e.getMember(), Roles.SUPER_ADMIN) && !RuntimeEditor.isAllowEditOtherUserWarn()) {
      e.replyError(C.permMsg(Roles.SUPER_ADMIN) + " (For editing other staff members' warnings.)");
      return;
    }

    if (warningManager.updateWarningReason(id, newReason)) {
      e.replySuccess("Edited warning!");
    } else {
      e.replyError("Error while editing warning!");
    }
  }
}
