package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;

public class PardonCommand extends CommandBase {

    public PardonCommand() {
        super("pardon", "<user id>", "Pardons/Unbans the target user.", CommandCategory.STAFF, Roles.MODERATOR);
        this.aliases = new String[]{"unban"};
    }

    @Override
    protected void executeCommand(CommandEvent e) {
        String[] args = e.getArgs().split(" ");
        if (e.getArgs().length() > 2) {
            if (args.length >= 1) {
                C.getCtrl(e).unban(args[0]).reason("Pardoned by Moderator: " + e.getMember().getUser().getName()).queue();
                e.replySuccess("User U(" + args[0] + ") has been *forgivably pardoned*  by " + e.getMember().getEffectiveName());
            } else {
                e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
            }
        } else {
            e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
        }
    }
}
