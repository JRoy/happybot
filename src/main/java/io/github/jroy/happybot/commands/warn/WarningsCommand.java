package io.github.jroy.happybot.commands.warn;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.sql.WarningManager;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Channels;
import io.github.jroy.happybot.util.Roles;
import io.github.jroy.happybot.util.RuntimeEditor;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WarningsCommand extends CommandBase {

  private WarningManager warningManager;

  public WarningsCommand(WarningManager warningManager) {
    super("warnings", "<user>", "Provides list of target user's warnings.", CommandCategory.STAFF);
    this.aliases = new String[]{"warns"};
    this.warningManager = warningManager;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (C.hasRole(e.getMember(), Roles.HELPER) || RuntimeEditor.isPermittingWarningExposement()) {
      if (!C.containsMention(e)) {
        try {
          WarningToken token = grabWarnings(e.getAuthor());
          if (token.getWarnings() == 0) {
            e.replyError(C.bold("Correct Usage:") + " ^" + name + " **<user>**");
            return;
          }
          e.reply(token.getBuilder().toString().replace("'s Warnings", "'s Warning [" + token.getWarnings() + "]"));
          return;
        } catch (SQLException e1) {
          e.replyError(C.bold("Correct Usage:") + " ^" + name + " **<user>**");
        }
        e.replyError(C.bold("Correct Usage:") + " ^" + name + " **<user>**");
        return;
      }

      String channelId = e.getChannel().getId();

      if ((Channels.RANDOM.getId().equalsIgnoreCase(channelId) || Channels.ARCHIVED_RANDOM.getId().equalsIgnoreCase(channelId) || Channels.GAMBLE.getId().equalsIgnoreCase(channelId) || Channels.MUSIC_REQUEST.getId().equalsIgnoreCase(channelId)) && !C.hasRole(e.getMember(), Roles.SUPER_ADMIN) && !RuntimeEditor.isPermittingWarningExposement()) {
        e.reply("Please use a staff channel to view user warnings...");
        return;
      }

      try {
        WarningToken token = grabWarnings(C.getMentionedMember(e).getUser());
        if (token.getWarnings() == 0) {
          e.replyError("Target User has no warnings.");
          return;
        }
        e.reply(token.getBuilder().toString().replace("'s Warnings", "'s Warning [" + token.getWarnings() + "]"));
      } catch (SQLException e1) {
        e.replyError("Oof Error: " + e1.getMessage());
      }
    } else {
      e.reply(C.permMsg(Roles.HELPER));
    }
  }

  private WarningToken grabWarnings(User user) throws SQLException {
    ResultSet resultSet = warningManager.fetchWarnings(user.getId());
    StringBuilder builder = new StringBuilder();
    builder.append(user.getName()).append("'s Warnings\n");
    int warnings = 0;
    while (resultSet.next()) {
      Member staffMem = C.getGuild().getMemberById(resultSet.getString("staffid"));
      if (staffMem != null) {
        builder.append("#").append(resultSet.getString("id")).append(" ")
            .append(C.bold(C.getFullName(staffMem.getUser()))).append(" - ").append(C.bold(resultSet.getString("reason")))
            .append(" (").append(resultSet.getTimestamp("time").toString()).append(")")
            .append("\n");
        warnings++;
      }
    }
    return new WarningToken(builder, warnings);
  }

  private class WarningToken {

    private StringBuilder builder;
    private int warnings;

    WarningToken(StringBuilder builder, int warnings) {
      this.builder = builder;
      this.warnings = warnings;
    }

    private StringBuilder getBuilder() {
      return builder;
    }

    private int getWarnings() {
      return warnings;
    }
  }

}
