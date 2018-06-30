package io.github.jroy.happybot.commands.report;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.sql.ReportManager;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Channels;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;

import java.awt.*;
import java.sql.SQLException;

public class ReportCommand extends CommandBase {

    private final ReportManager reportManager;

    public ReportCommand(ReportManager reportManager) {
        super("report", "<user> <reason>", "Sends a report to the staff members!", CommandCategory.GENERAL);
        this.reportManager = reportManager;
    }

    @Override
    protected void executeCommand(CommandEvent e) {
        if (!C.containsMention(e)) {
            e.replyError(C.bold("Correct Usage:") + " ^" + name + " **<user>** <reason>");
            return;
        }
        if (e.getArgs().replaceAll("<(.*?)>", "").isEmpty()) {
            e.replyError(C.bold("Correct Usage:") + " ^" + name + " <user> **<reason>**");
            return;
        }

        String reason = e.getArgs().replaceFirst("<(.*?)> ", "");
        Member target = C.getMentionedMember(e);

        try {
            int id = reportManager.spawnReport(target.getUser().getId(), e.getMember().getUser().getId(), e.getChannel().getId(), reason);
            Channels.REPORT.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("New Report")
                    .setDescription(":paperclip: " + C.bold("Report #" + id + " has been created.") + "\n:arrow_forward: " + C.bold("Reported User: ") + target.getAsMention() + "\n:arrow_forward: " + C.bold("Reporter: " + e.getMember().getAsMention()) + "\n:page_facing_up: " + C.bold("Reason: ") + C.code(reason) + "\n:hash: " + C.bold("Channel: <#" + e.getChannel().getId() + ">"))
                    .setColor(Color.GREEN)
                    .setThumbnail(target.getUser().getAvatarUrl())
                    .build()
            ).queue();
            e.replySuccess("Submitted Report #" + id + " !");
        } catch (SQLException e1) {
            e.replyError("Oof Error: " + e1.getMessage());
        }
    }
}
