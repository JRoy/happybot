package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.core.entities.Member;

public class DemoteCommand extends CommandBase {

  public DemoteCommand() {
    super("demote", "<user>", "Demotes target user from the staff team.", CommandCategory.STAFF, Roles.RECRUITER);
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (e.getMessage().getMentionedUsers().size() == 1) {
      e.reply("Please wait while we look how to demote " + C.getMentionedMember(e).getAsMention() + "!");
      Member member = C.getMentionedMember(e);
      if (!C.hasRole(member, Roles.HELPER)) {
        e.replyError("User is not on the staff team!");
        return;
      }
      removeIfHasRole(member, Roles.SUPER_ADMIN);
      removeIfHasRole(member, Roles.MODERATOR);
      removeIfHasRole(member, Roles.HELPER);
      e.replySuccess("User has been demoted!");
    } else {
      e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
    }
  }

  private void removeIfHasRole(Member member, Roles role) {
    if (C.hasRole(member, role)) {
      C.removeRole(member, role);
    }
  }
}
