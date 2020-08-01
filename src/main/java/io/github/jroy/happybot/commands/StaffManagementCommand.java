package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;

public class StaffManagementCommand extends CommandBase {

  public StaffManagementCommand() {
    super("staffmng", "<deny/deny-level/approve> <user>", "A command to help Recruiters with their job.", CommandCategory.STAFF, Roles.SUPER_ADMIN);
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (!e.getArgs().isEmpty()) {
      if (e.getArgs().startsWith("deny ")) {
        if (e.getMessage().getMentionedUsers().size() == 1) {
          C.privChannel(C.getMentionedMember(e),
              "Hey! I am sorry to say but your application has been denied due to **lack of detail**. You may reapply in 5 days!");
          e.replySuccess("Application Denied!");
        } else {
          e.replyError("^staffmng <deny/deny-level/approve> <user>");
        }
      } else if (e.getArgs().startsWith("deny-level ")) {
        if (e.getMessage().getMentionedUsers().size() == 1) {
          C.privChannel(C.getMentionedMember(e),
              "Hey! I am sorry to say but your application has been denied due to lack of **community involvement**. You may reapply in 5 days!");
          e.replySuccess("Application Denied!");
        } else {
          e.replyError("^staffmng <deny/deny-level/approve> <user>");
        }
      } else if (e.getArgs().startsWith("approve ")) {
        C.privChannel(C.getMentionedMember(e),
            "Hey! I am sorry to say but your application has been APPROVED!!!111 Your rank will be applied very soon ;)");
        e.replySuccess("Application Approved!");
      } else {
        e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
      }
    } else {
      e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
    }
  }
}
