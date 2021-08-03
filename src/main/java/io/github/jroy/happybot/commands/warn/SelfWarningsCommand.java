package io.github.jroy.happybot.commands.warn;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.sql.WarningManager;
import io.github.jroy.happybot.util.C;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SelfWarningsCommand extends CommandBase {

  private final WarningManager warningManager;

  public SelfWarningsCommand(WarningManager warningManager) {
    super("mywarns", null, "Direct Messages you a list of your warnings.", CommandCategory.GENERAL);
    this.aliases = new String[]{"mywarnings"};
    this.warningManager = warningManager;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    e.reply("Providing Memes in DMs");
    try {
      ResultSet resultSet = warningManager.fetchWarnings(e.getEvent().getAuthor().getId());
      StringBuilder builder = new StringBuilder();
      User targetM = e.getEvent().getAuthor();
      int count = 0;
      while (resultSet.next()) {
        Member staffMem = C.getGuild().getMemberById(resultSet.getString("staffid"));
        if (staffMem != null) {
          count++;
          builder.append("#").append(resultSet.getString("id")).append(" ").append(C.bold(staffMem.getUser().getName() + "#" + staffMem.getUser().getDiscriminator())).append(" - ").append(C.bold(resultSet.getString("reason"))).append("\n");
        }
      }
      e.replyInDm(targetM.getName() + "'s Warnings [" + count + "]\n" + builder);
    } catch (SQLException e1) {
      e.replyError("Oof Error: " + e1.getMessage());
    }
  }
}
