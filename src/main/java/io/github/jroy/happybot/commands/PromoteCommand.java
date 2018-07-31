package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.core.entities.Member;

public class PromoteCommand extends CommandBase {

  public PromoteCommand() {
    super("promote", "<user>", "Promotes the target user up the staff hierarchy", CommandCategory.STAFF, Roles.RECRUITER);
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (e.getMessage().getMentionedUsers().size() == 1) {
      e.reply("Please wait while we look how to promote " + C.getMentionedMember(e).getAsMention() + "!");
      Member member = C.getMentionedMember(e);
      promoteMember(member, e);
    } else {
      e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
    }
  }

  private void promoteMember(Member member, CommandEvent event) {
    if (C.hasRole(member, Roles.SUPER_ADMIN)) {
      event.replySuccess("User is already the on highest level of promotion!");
    } else if (promoteIfHasRole(Roles.MODERATOR, Roles.SUPER_ADMIN, member, event)
        && promoteIfHasRole(Roles.HELPER, Roles.MODERATOR, member, event)
        && promoteIfHasRole(Roles.FANS, Roles.HELPER, member, event)) {
      event.replyError("User has a malformed role!");
    }
  }

  /**
   * Promotes a member if they have the required role
   *
   * @return True if the member was promoted
   */
  private boolean promoteIfHasRole(Roles requiredRole, Roles promotionRole, Member member, CommandEvent event) {
    if (C.hasRole(member, requiredRole)) {
      event.replySuccess("User has been promoted to " + C.bold(promotionRole.toString()) + "!");
      event.getGuild().getController().addSingleRoleToMember(member, promotionRole.getRole()).reason("User Promotion!").queue();
      return false;
    }
    return true;
  }

}
