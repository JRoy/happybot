package com.wheezygold.happybot.commands.report;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.wheezygold.happybot.sql.ReportManager;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Channels;
import com.wheezygold.happybot.util.Roles;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;

public class HandleReportCommand extends Command {

    private final ReportManager reportManager;

    public HandleReportCommand(ReportManager reportManager) {
        this.name = "handlereport";
        this.aliases = new String[]{"hreport", "hr"};
        this.help = "Accept or deny a report.";
        this.arguments = "<accept/deny> <id> <reason>";
        this.guildOnly = true;
        this.category = new Category("Staff Tools");
        this.reportManager = reportManager;
    }

    @Override
    protected void execute(CommandEvent e) {
        if (C.hasRole(e.getMember(), Roles.HELPER)) {
            if (e.getArgs().isEmpty()) {
                e.replyError("**Correct Usage:** ^" + name + " " + arguments);
                return;
            }

            String[] args = e.getArgs().split("[ ]");

            if (args.length < 3) {
                e.replyError("**Correct Usage:** ^" + name + " " + arguments);
                return;
            }

            String reason =  e.getArgs().replaceFirst(args[0] + " " + args[1] + " ", "");

            if (reason.isEmpty() || !StringUtils.isNumeric(args[1]) || !(args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("deny") )) {
                e.replyError("**Correct Usage:** ^" + name + " " + arguments);
                return;
            }

            int targetReport = Integer.parseInt(args[1]);

            if (!reportManager.isValidReport(targetReport)) {
                e.replyError("Invalid Report ID!");
                return;
            }

            if (reportManager.getReportStatus(targetReport) != 0) {
                e.replyError("Report is not currently pending!");
                return;
            }

            if (args[0].equalsIgnoreCase("accept")) {
                reportManager.acceptReport(targetReport, e.getMember().getUser().getId(), reason);
                Channels.REPORT.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Report Accepted")
                        .setDescription(":paperclip: " + C.bold("Report #" + String.valueOf(targetReport) + " has been accepted.") + "\n:arrow_forward: " + C.bold("Staff Member: ") + e.getMember().getAsMention() + "\n:page_facing_up: " + C.bold("Reason: ") + C.smallCodeblock(reason))
                        .setColor(Color.GREEN)
                        .setThumbnail(e.getMember().getUser().getAvatarUrl())
                        .build()
                ).queue();
                User tU = e.getJDA().getUserById(reportManager.getReportTarget(targetReport));
                C.privChannel(e.getGuild().getMemberById(reportManager.getReportAuthor(targetReport)), "Your report against " + tU.getName()+"#"+tU.getDiscriminator() + " has been accepted!\nReason: " + C.codeblock(reason));
                e.replySuccess("Report has been accepted!");
            } else if (args[0].equalsIgnoreCase("deny")) {
                reportManager.denyReport(targetReport, e.getMember().getUser().getId(), reason);
                Channels.REPORT.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Report Denied")
                        .setDescription(":paperclip: " + C.bold("Report #" + String.valueOf(targetReport) + " has been denied.") + "\n:arrow_forward: " + C.bold("Staff Member: ") + e.getMember().getAsMention() + "\n:page_facing_up: " + C.bold("Reason: ") + C.smallCodeblock(reason))
                        .setColor(Color.GREEN)
                        .setThumbnail(e.getMember().getUser().getAvatarUrl())
                        .build()
                ).queue();
                User tU = e.getJDA().getUserById(reportManager.getReportTarget(targetReport));
                C.privChannel(e.getGuild().getMemberById(reportManager.getReportAuthor(targetReport)), "Your report against " + tU.getName()+"#"+tU.getDiscriminator() + " has been denied!\nReason: " + C.codeblock(reason));
                e.replySuccess("Report has been denied!");
            }
        } else {
            e.replyError(C.permMsg(Roles.HELPER));
        }
    }
}
