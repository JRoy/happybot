package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Roles;

public class PardonCommand extends Command {

    public PardonCommand() {
        this.name = "pardon";
        this.help = "Pardons target user from the server.";
        this.arguments = "<user id>";
        this.guildOnly = true;
        this.category = new Category("Staff Tools");
    }

    @Override
    protected void execute(CommandEvent e) {
        if (C.hasRole(e.getMember(), Roles.MODERATOR)) {
            String[] args = e.getArgs().split(" ");
            if (e.getArgs().length() > 2) {
                if (args.length >= 1) {
                    C.getCtrl(e).unban(args[0]).reason("Pardoned by Moderator: " + e.getMember().getUser().getName()).queue();
                    e.replySuccess("User U(" + args[0] + ") has been *forgivably pardoned*  by " + e.getMember().getEffectiveName());
                } else {
                    e.replyError("**Correct Usage:** ^" + name + " " + arguments);
                }
            } else {
                e.replyError("**Correct Usage:** ^" + name + " " + arguments);
            }
        } else {
            e.replyError(C.permMsg(Roles.MODERATOR));
        }
    }
}
