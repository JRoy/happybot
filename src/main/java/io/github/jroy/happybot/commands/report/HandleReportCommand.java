package io.github.jroy.happybot.commands.report;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.sql.ReportManager;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Channels;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.util.Objects;

public class HandleReportCommand extends CommandBase {

  private final ReportManager reportManager;

  public HandleReportCommand(ReportManager reportManager) {
    super("handlereport", "<accept/deny> <id> <reason>", "Accept or deny a report.", CommandCategory.STAFF, Roles.HELPER);
    this.aliases = new String[]{"hr", "hreport"};
    this.reportManager = reportManager;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (e.getArgs().isEmpty()) {
      e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
      return;
    }

    String[] args = e.getSplitArgs();

    if (args.length < 3) {
      e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
      return;
    }

    String reason = e.getArgs().replaceFirst(args[0] + " " + args[1] + " ", "");

    if (reason.isEmpty() || !StringUtils.isNumeric(args[1]) || !(args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("deny"))) {
      e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
      return;
    }

    int targetReport = Integer.parseInt(args[1]);

    if (reportManager.isInvalidReport(targetReport)) {
      e.replyError("Invalid Report ID!");
      return;
    }

    if (reportManager.getReportStatus(targetReport) != 0) {
      e.replyError("Report is not currently pending!");
      return;
    }

    if (args[0].equalsIgnoreCase("accept")) {
      reportManager.acceptReport(targetReport, e.getMember().getUser().getId(), reason);
      Channels.STAFF_QUEUE.getChannel().sendMessage(new EmbedBuilder()
          .setTitle("Report Accepted")
          .setDescription(":paperclip: " + C.bold("Report #" + targetReport + " has been accepted.") + "\n:arrow_forward: " + C.bold("Staff Member: ") + e.getMember().getAsMention() + "\n:page_facing_up: " + C.bold("Reason: ") + C.code(reason))
          .setColor(Color.GREEN)
          .setThumbnail(e.getMember().getUser().getAvatarUrl())
          .build()
      ).queue();
      User tU = e.getJDA().getUserById(reportManager.getReportTarget(targetReport));
      C.privChannel(Objects.requireNonNull(e.getGuild().getMemberById(reportManager.getReportAuthor(targetReport))), "Your report against " + Objects.requireNonNull(tU).getName() + "#" + tU.getDiscriminator() + " has been accepted!\nReason: " + C.codeblock(reason));
      e.replySuccess("Report has been accepted!");
    } else if (args[0].equalsIgnoreCase("deny")) {
      reportManager.denyReport(targetReport, e.getMember().getUser().getId(), reason);
      Channels.STAFF_QUEUE.getChannel().sendMessage(new EmbedBuilder()
          .setTitle("Report Denied")
          .setDescription(":paperclip: " + C.bold("Report #" + targetReport + " has been denied.") + "\n:arrow_forward: " + C.bold("Staff Member: ") + e.getMember().getAsMention() + "\n:page_facing_up: " + C.bold("Reason: ") + C.code(reason))
          .setColor(Color.GREEN)
          .setThumbnail(e.getMember().getUser().getAvatarUrl())
          .build()
      ).queue();
      User tU = e.getJDA().getUserById(reportManager.getReportTarget(targetReport));
      C.privChannel(Objects.requireNonNull(e.getGuild().getMemberById(reportManager.getReportAuthor(targetReport))), "Your report against " + Objects.requireNonNull(tU).getName() + "#" + tU.getDiscriminator() + " has been denied!\nReason: " + C.codeblock(reason));
      e.replySuccess("Report has been denied!");
    }
  }
}
