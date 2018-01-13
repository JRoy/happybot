package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.sql.ReportManager;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Channels;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;

import java.awt.*;
import java.sql.SQLException;

public class ReportCommand extends Command {

    private final ReportManager reportManager;

    public ReportCommand(ReportManager reportManager) {
        this.name = "report";
        this.help = "Sends a report to the staff members!";
        this.arguments = "<user> <reason>";
        this.guildOnly = true;
        this.category = new Category("General");
        this.reportManager = reportManager;
    }

    @Override
    protected void execute(CommandEvent e) {
        if (!C.containsMention(e)) {
            e.replyError("**Correct Usage:** ^" + name + " **<user>** <reason>");
            return;
        }
        if (e.getArgs().replaceAll("<(.*?)>", "").isEmpty()) {
            e.replyError("**Correct Usage:** ^" + name + " <user> **<reason>**");
            return;
        }

        String reason = e.getArgs().replaceFirst("<(.*?)> ", "");
        Member target = C.getMentionedMember(e);

        try {
            int id = reportManager.spawnReport(target.getUser().getId(), e.getMember().getUser().getId(), e.getChannel().getId(), reason);
            Channels.REPORT.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("New Report")
                    .setDescription(":paperclip: " + C.bold("Report #" + String.valueOf(id) + " has been created.") + "\n:arrow_forward: " + C.bold("Reported User: ") + target.getAsMention() + "\n:arrow_forward: " + C.bold("Reporter: " + e.getMember().getAsMention()) + "\n:page_facing_up: " + C.bold("Reason: ") + C.smallCodeblock(reason) + "\n:hash: " + C.bold("Channel: <#" + e.getChannel().getId() + ">"))
                    .setColor(Color.GREEN)
                    .setThumbnail(target.getUser().getAvatarUrl())
                    .build()
            ).queue();
            e.replySuccess("Submitted Report #" + String.valueOf(id) + " !");
        } catch (SQLException e1) {
            e.replyError("Oof Error: " + e1.getMessage());
        }
    }
}
