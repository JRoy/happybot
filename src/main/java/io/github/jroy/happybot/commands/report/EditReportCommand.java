package io.github.jroy.happybot.commands.report;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.sql.ReportManager;
import io.github.jroy.happybot.sql.ReportToken;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;
import org.apache.commons.lang3.StringUtils;

public class EditReportCommand extends CommandBase {

    private final ReportManager reportManager;

    public EditReportCommand(ReportManager reportManager) {
        super("editreport", "<id> <reason>", "Edit a report's handle reason.", CommandCategory.STAFF, Roles.HELPER);
        this.aliases = new String[]{"ereport", "er"};
        this.reportManager = reportManager;
    }

    @Override
    protected void executeCommand(CommandEvent e) {
        if (e.getArgs().isEmpty()) {
            e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
            return;
        }

        String[] args = e.getSplitArgs();

        if (args.length < 1) {
            e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
            return;
        }

        String reason =  e.getArgs().replaceFirst(args[0] + " ", "");

        if (!StringUtils.isNumeric(args[0]) || reason.isEmpty()) {
            e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
            return;
        }

        int targetReport = Integer.parseInt(args[0]);

        if (!reportManager.isValidReport(targetReport)) {
            e.replyError("Invalid Report ID!");
            return;
        }

        ReportToken token = reportManager.getReportAsToken(targetReport);

        if (token == null) {
            e.replyError("Could not fetch the report data!");
            return;
        }

        if (token.getStatus() == 0) {
            e.replyError("This report is currently still pending, please handle the report first!");
            return;
        }

        if (!token.getHandler().getId().equals(e.getMember().getUser().getId()) && !C.hasRoleStrict(e.getMember(), Roles.DEVELOPER)) {
            e.replyError("You may not edit another user's report handle reasons! (Dev+)");
            return;
        }

        reportManager.setHandleReason(targetReport, reason);

        e.replySuccess("Updated the handle reason!");
    }
}
