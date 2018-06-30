package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.core.entities.Member;

public class BanCommand extends CommandBase {
    public BanCommand() {
        super("ban", "<user mention> <reason>", "Bans target user from server.", CommandCategory.STAFF, Roles.MODERATOR);
        this.aliases = new String[]{"begone"};
    }

    @Override
    protected void executeCommand(CommandEvent e) {
        if (C.containsMention(e)) {
            if (e.getArgs().replaceAll("<(.*?)>", "").isEmpty()) {
                e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
                return;
            }
            String reason = e.getArgs().replaceFirst("<(.*?)> ", "");
            Member target = C.getMentionedMember(e);

            if (target.getUser().getId().equalsIgnoreCase(e.getMember().getUser().getId())) {
                e.replyError("You may not ban yourself! :wink:");
                return;
            }

            C.privChannel(target, "Banned with Reason: " + reason);

            C.getCtrl(e).ban(target.getUser(), 7, "Banned by Moderator: " + e.getMember().getUser().getName()).reason("Banned by Moderator: " + e.getMember().getUser().getName() + ". With Reason: " + reason).queue();
            e.replySuccess("User " + C.getFullName(target.getUser()) + " has been **FRIGGING BANNED** by " + e.getMember().getEffectiveName());
        } else {
            e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
        }
    }
}
