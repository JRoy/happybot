package io.github.jroy.happybot.commands.og;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.api.entities.Member;

public class OgCommand extends CommandBase {

  public OgCommand() {
    super("og", "<user>", "Toggle's a user OG Stats.", CommandCategory.STAFF, Roles.MODERATOR);
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (e.getMessage().getMentionedUsers().size() == 1) {
      Member u = C.getMentionedMember(e);
      if (C.hasRole(u, Roles.OG)) {
        C.removeRole(u, Roles.OG);
        e.replySuccess(u.getUser().getAsMention() + " is no longer OG!");
      } else {
        C.giveRole(u, Roles.OG);
        e.replySuccess(u.getUser().getAsMention() + " has become OG!");
      }
    } else {
      e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
    }
  }
}
