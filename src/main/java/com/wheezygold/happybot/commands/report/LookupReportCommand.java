package com.wheezygold.happybot.commands.report;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.wheezygold.happybot.sql.ReportManager;
import com.wheezygold.happybot.sql.ReportToken;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Roles;
import net.dv8tion.jda.core.EmbedBuilder;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;

public class LookupReportCommand extends Command {

    private ReportManager reportManager;

    public LookupReportCommand(ReportManager reportManager) {
        this.name = "lookupreport";
        this.aliases = new String[]{"lreport", "lr"};
        this.help = "Lookup a report from the database.";
        this.arguments = "<id>";
        this.guildOnly = true;
        this.category = new Category("Staff Tools");
        this.reportManager = reportManager;
    }

    @Override
    protected void execute(CommandEvent e) {
        if (C.hasRole(e.getMember(), Roles.HELPER)) {
            long startMills = System.currentTimeMillis();
            if (!StringUtils.isNumeric(e.getArgs())) {
                e.replyError("**Correct Usage:** ^" + name + " " + arguments);
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
            builder.append(":paperclip: ").append(C.bold("Report #" + e.getArgs())).append(" (Status ").append(String.valueOf(token.getStatus())).append(")");
            builder.append("\n:arrow_forward: ").append(C.bold("Reported User: ")).append(token.getTarget().getAsMention());
            builder.append("\n:arrow_forward: ").append(C.bold("Reporter: " + token.getReporter().getAsMention()));
            builder.append("\n:page_facing_up: ").append(C.bold("Reason: ")).append(C.smallCodeblock(token.getReason()));
            builder.append("\n:hash: ").append(C.bold("Channel: <#" + token.getChannelId() + ">"));
            switch (token.getStatus()) {
                case 0: {
                    builder.append("\n:information_source: ").append(C.bold("Report has not been handled!"));
                    break;
                }
                case 1: {
                    builder.append("\n:information_source: ").append(C.bold("Report has been marked as Accepted."));
                    builder.append("\n:construction_worker: ").append(C.bold("Staff Member: ")).append(token.getHandler().getAsMention());
                    builder.append("\n:clipboard: ").append(C.bold("Accept Reason: ")).append(C.smallCodeblock(token.getHandleReason()));
                    break;
                }
                case 2: {
                    builder.append("\n:information_source: ").append(C.bold("Report has been marked as Denied."));
                    builder.append("\n:construction_worker: ").append(C.bold("Staff Member: ")).append(token.getHandler().getAsMention());
                    builder.append("\n:clipboard: ").append(C.bold("Deny Reason: ")).append(C.smallCodeblock(token.getHandleReason()));
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
                    .setFooter("happyreports: Lookup took " + String.valueOf(System.currentTimeMillis() - startMills) + "ms.", null)
                    .build());
        } else {
            e.replyError(C.permMsg(Roles.HELPER));
        }
    }
}
