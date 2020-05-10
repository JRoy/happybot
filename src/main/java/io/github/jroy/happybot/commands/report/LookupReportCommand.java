package io.github.jroy.happybot.commands.report;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.sql.ReportManager;
import io.github.jroy.happybot.sql.ReportToken;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;

public class LookupReportCommand extends CommandBase {

  private final ReportManager reportManager;

  public LookupReportCommand(ReportManager reportManager) {
    super("lookupreport", "<id>", "Lookup a report from the database.", CommandCategory.STAFF, Roles.HELPER);
    this.aliases = new String[]{"lreport", "lr"};
    this.reportManager = reportManager;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    long startMills = System.currentTimeMillis();
    if (!StringUtils.isNumeric(e.getArgs())) {
      e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
      return;
    }

    int targetReport = Integer.parseInt(e.getArgs());

    if (!reportManager.isValidReport(targetReport)) {
      e.replyError("Invalid Report ID!");
      return;
    }

    ReportToken token = reportManager.getReportAsToken(targetReport);

    if (token == null) {
      e.replyError("Error while looking up report!");
      return;
    }

    StringBuilder builder = new StringBuilder();
    builder.append(":paperclip: ").append(C.bold("Report #" + e.getArgs())).append(" (Status ").append(token.getStatus()).append(")");
    builder.append("\n:arrow_forward: ").append(C.bold("Reported User: ")).append(token.getTarget().getAsMention());
    builder.append("\n:arrow_forward: ").append(C.bold("Reporter: " + token.getReporter().getAsMention()));
    builder.append("\n:page_facing_up: ").append(C.bold("Reason: ")).append(C.code(token.getReason()));
    builder.append("\n:hash: ").append(C.bold("Channel: <#" + token.getChannelId() + ">"));
    switch (token.getStatus()) {
      case 0: {
        builder.append("\n:information_source: ").append(C.bold("Report has not been handled!"));
        break;
      }
      case 1: {
        builder.append("\n:information_source: ").append(C.bold("Report has been marked as Accepted."));
        builder.append("\n:construction_worker: ").append(C.bold("Staff Member: ")).append(token.getHandler().getAsMention());
        builder.append("\n:clipboard: ").append(C.bold("Accept Reason: ")).append(C.code(token.getHandleReason()));
        break;
      }
      case 2: {
        builder.append("\n:information_source: ").append(C.bold("Report has been marked as Denied."));
        builder.append("\n:construction_worker: ").append(C.bold("Staff Member: ")).append(token.getHandler().getAsMention());
        builder.append("\n:clipboard: ").append(C.bold("Deny Reason: ")).append(C.code(token.getHandleReason()));
        break;
      }
      default: {
        builder.append("\n:information_source: ").append(C.bold("Report status code is malformed!"));
        break;
      }
    }

    e.reply(new EmbedBuilder()
        .setTitle("Report Lookup")
        .setDescription(builder.toString())
        .setColor(Color.GREEN)
        .setFooter("happyreports: Lookup took " + (System.currentTimeMillis() - startMills) + "ms.", null)
        .build());
  }
}
