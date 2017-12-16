package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Channels;
import com.wheezygold.happybot.util.Roles;
import com.wheezygold.happybot.util.WarningManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;

import java.awt.*;
import java.sql.SQLException;

public class WarnCommand extends Command {

    private WarningManager warningManager;

    public WarnCommand(WarningManager warningManager) {
        this.name = "warn";
        this.arguments = "<user> <reason>";
        this.help = "Warns the target user.";
        this.guildOnly = true;
        this.category = new Category("Staff Tools");
        this.aliases = new String[]{"warning"};
        this.warningManager = warningManager;
    }

    @Override
    protected void execute(CommandEvent e) {
        if (C.hasRole(e.getMember(), Roles.HELPER)) {
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
                warningManager.spawnWarning(target.getUser().getId(), e.getMember().getUser().getId(), reason);
                C.privChannel(target, "You have been warned for: " + C.bold(reason) + "! To review the rules please type `^rules` in the random channel.");
                e.reply("Warned User!");
                Channels.LOG.getChannel().sendMessage(new EmbedBuilder()
                        .setAuthor(e.getMember().getUser().getName() + "#" + e.getMember().getUser().getDiscriminator(), null,  e.getMember().getUser().getAvatarUrl())
                        .setColor(Color.YELLOW)
                        .setThumbnail(target.getUser().getAvatarUrl())
                        .setDescription("⚠ " + C.bold("Warned " + target.getUser().getName() + "#" + target.getUser().getDiscriminator()) + "\n:page_facing_up: " + C.bold("Reason: ") + reason )
                        .build()).queue();
            } catch (SQLException e1) {
                e.replyError("Oof Error: " + e1.getMessage());
            }
        } else {
            e.replyError(C.permMsg(Roles.HELPER));
        }
    }
}
