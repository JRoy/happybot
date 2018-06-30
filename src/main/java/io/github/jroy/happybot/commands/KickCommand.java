package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.core.entities.Member;

public class KickCommand extends CommandBase {

    public KickCommand() {
        super("kick", "<user mention> <reason>", "Kicks target user from the server.", CommandCategory.STAFF, Roles.HELPER);
        this.aliases = new String[]{"deport"};
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
                e.replyError("You may not kick yourself! :wink:");
                return;
            }

            C.getCtrl(e).kick(target).reason("Kicked by Moderator: " + e.getMember().getUser().getName() + ". With Reason: " + reason).queue();
            e.replySuccess("User " + C.getFullName(target.getUser()) + " has been **FLIPPIN KICKED** by " + e.getMember().getEffectiveName());
        } else {
            e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
        }
    }
}
