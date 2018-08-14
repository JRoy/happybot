package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;
import io.github.jroy.happybot.util.RuntimeEditor;
import org.apache.commons.lang3.StringUtils;

public class RuntimeCommand extends CommandBase {

  public RuntimeCommand() {
    super("runtime",
        "<selfGilds/evalOwner/pingIssueClose/filterAdvert/publicWarns/editUserWarns/gambleMax/gambleJackpot/allowStaffSubBypass>",
        "Edits varibles for commands during runtime.",
        CommandCategory.BOT,
        Roles.DEVELOPER);
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    String[] args = e.getSplitArgs();
    if (args.length == 2) {
      String target = args[0];
      String value = args[1];
      boolean valid = true;
      switch (target) {
        case "selfGilds": {
          if (C.containsBool(value)) {
            RuntimeEditor.setAllowSelfGilds(Boolean.parseBoolean(value));
          } else {
            valid = false;
          }
          break;
        }
        case "evalOwner": {
          if (C.containsBool(value)) {
            RuntimeEditor.setEvalOwnerOnly(Boolean.parseBoolean(value));
          } else {
            valid = false;
          }
          break;
        }
        case "pingIssueClose": {
          if (C.containsBool(value)) {
            RuntimeEditor.setPingIssueClose(Boolean.parseBoolean(value));
          } else {
            valid = false;
          }
          break;
        }
        case "filterAdvert": {
          if (C.containsBool(value)) {
            RuntimeEditor.setFilteringAdverts(Boolean.parseBoolean(value));
          } else {
            valid = false;
          }
          break;
        }
        case "editUserWarns": {
          if (C.containsBool(value)) {
            RuntimeEditor.setAllowEditOtherUserWarn(Boolean.parseBoolean(value));
          } else {
            valid = false;
          }
          break;
        }
        case "publicWarns": {
          if (C.containsBool(value)) {
            RuntimeEditor.setPermittingWarningExposement(Boolean.parseBoolean(value));
          } else {
            valid = false;
          }
          break;
        }
        case "gambleMax": {
          if (StringUtils.isNumeric(value)) {
            RuntimeEditor.setGambleMax(Integer.parseInt(value));
          } else {
            valid = false;
          }
          break;
        }
        case "gambleJackpot": {
          try {
            float chance = Float.parseFloat(value);
            if (chance < 0 || chance > 1) {
              e.replyError("Chance must be between 0 and 1 (0 to disable)");
              return;
            }

            RuntimeEditor.setGambleJackpot(chance);
          } catch (NumberFormatException ignored) {
            valid = false;
          }
          break;
        }
        case "allowStaffSubBypass": {
          if (C.containsBool(value)) {
            RuntimeEditor.setAllowStaffSubBypass(Boolean.parseBoolean(value));
          } else {
            valid = false;
          }
          break;
        }
        default: {
          e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
          return;
        }
      }
      if (valid) {
        e.replySuccess(":gear: Updated value to **" + value.toLowerCase() + "**!");
        return;
      }
      e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
    } else {
      e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
    }
  }
}
