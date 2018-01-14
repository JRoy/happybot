package com.wheezygold.happybot.commands.report;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.sql.ReportManager;
import com.wheezygold.happybot.sql.ReportToken;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Roles;
import org.apache.commons.lang3.StringUtils;

public class EditReportCommand extends Command {

    private final ReportManager reportManager;

    public EditReportCommand(ReportManager reportManager) {
        this.name = "editreport";
        this.aliases = new String[]{"ereport", "er"};
        this.help = "Edit a report's handle reason.";
        this.arguments = "<id> <reason>";
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

            if (args.length < 1) {
                e.replyError("**Correct Usage:** ^" + name + " " + arguments);
                return;
            }

            String reason =  e.getArgs().replaceFirst(args[0] + " ", "");

            if (!StringUtils.isNumeric(args[0]) || reason.isEmpty()) {
                e.replyError("**Correct Usage:** ^" + name + " " + arguments);
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

            if (!token.getHandler().getId().equals(e.getMember().getUser().getId()) && !C.hasRole(e.getMember(), Roles.DEVELOPER)) {
                e.replyError("You may not edit another user's report handle reasons! (Dev+)");
                return;
            }

            reportManager.setHandleReason(targetReport, reason);

            e.replySuccess("Updated the handle reason!");

        } else {
            e.replyError(C.permMsg(Roles.HELPER));
        }
    }
}
