package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Roles;
import com.wheezygold.happybot.util.WarningManager;
import org.apache.commons.lang3.StringUtils;

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
                if (warningManager.deleteWarning(Integer.parseInt(e.getArgs()))) {
                    e.reply("Deleted warning!");
                } else {
                    e.reply("Warning could not be deleted!");
                }
            } else {
                e.replyError("**Correct Usage:** ^" + name + " <warning ID>");
            }
        } else {
            e.reply(C.permMsg(Roles.HELPER));
        }
    }
}
