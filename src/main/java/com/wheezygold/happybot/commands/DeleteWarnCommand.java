package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.Main;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Channels;
import com.wheezygold.happybot.util.Roles;
import com.wheezygold.happybot.util.WarningManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;

public class DeleteWarnCommand extends Command {

    private WarningManager warningManager;

    public DeleteWarnCommand(WarningManager warningManager) {
        this.name = "delwarn";
        this.aliases = new String[]{"delwarning", "deletewarn", "deletewarning", "dwarn"};
        this.arguments = "<warning ID>";
        this.help = "Deletes the target warning.";
        this.guildOnly = true;
        this.category = new Category("Staff Tools");
        this.warningManager = warningManager;
    }

    @Override
    protected void execute(CommandEvent e) {
        if (C.hasRole(e.getMember(), Roles.HELPER)) {
            if (!e.getArgs().isEmpty() && StringUtils.isNumeric(e.getArgs())) {
                int id = Integer.parseInt(e.getArgs());
                if (warningManager.isValidWarning(id)) {
                    User tUser = Main.getJda().getUserById(warningManager.getWarnTargetId(id));
                    Channels.LOG.getChannel().sendMessage(new EmbedBuilder()
                            .setAuthor(e.getMember().getUser().getName() + "#" + e.getMember().getUser().getDiscriminator(), null,  e.getMember().getUser().getAvatarUrl())
                            .setColor(Color.YELLOW)
                            .setThumbnail(tUser.getAvatarUrl())
                            .setDescription(":information_source: **Warning Deleted**\n" + "⚠ " + C.bold("Warned " + tUser.getName() + "#" + tUser.getDiscriminator()) + "\n:page_facing_up: " + C.bold("Reason: ") + warningManager.getWarnReason(id) + "\n:id: **Warn ID** " + String.valueOf(id))
                            .build()).queue();
                    if (warningManager.deleteWarning(id)) {
                        e.reply("Deleted warning!");
                    } else {
                        e.reply("Warning could not be deleted!");
                    }
                } else {
                    e.replyError("Invalid Warning!");
                }
            } else {
                e.replyError("**Correct Usage:** ^" + name + " <warning ID>");
            }
        } else {
            e.reply(C.permMsg(Roles.HELPER));
        }
    }
}
