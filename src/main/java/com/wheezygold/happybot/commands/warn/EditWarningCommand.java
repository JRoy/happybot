package com.wheezygold.happybot.commands.warn;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Roles;
import com.wheezygold.happybot.util.RuntimeEditor;
import com.wheezygold.happybot.sql.WarningManager;
import org.apache.commons.lang3.StringUtils;

public class EditWarningCommand extends Command {

    private WarningManager warningManager;

    public EditWarningCommand(WarningManager warningManager) {
        this.name = "editwarn";
        this.aliases = new String[]{"editwarning", "ewarn"};
        this.arguments = "<warning ID> <new reason>";
        this.help = "Edits the target warning.";
        this.guildOnly = true;
        this.category = new Category("Staff Tools");
        this.warningManager = warningManager;
    }

    @Override
    protected void execute(CommandEvent e) {
        if (C.hasRole(e.getMember(), Roles.HELPER)) {
            String[] args = e.getArgs().split("[ ]");
            if (args.length < 1) {
                e.replyError("**Correct Usage:** ^" + name + " **<warning ID> <new reason>**");
                return;
            }
            if (!StringUtils.isNumeric(args[0])) {
                e.replyError("**Correct Usage:** ^" + name + " **<warning ID>** <new reason>");
                return;
            }
            int id = Integer.parseInt(args[0]);
            String newReason = e.getArgs().replaceFirst(args[0] + " ", "");
            if (!warningManager.isValidWarning(id)) {
                e.replyError("**Correct Usage:** ^" + name + " **<warning ID>** <new reason>");
                return;
            }
            if (!warningManager.getWarnAuthorId(id).equals(e.getMember().getUser().getId()) && !C.hasRole(e.getMember(), Roles.SUPER_ADMIN) && !RuntimeEditor.isAllowEditOtherUserWarn()) {
                e.replyError(C.permMsg(Roles.SUPER_ADMIN) + " (For editing other staff members warnings.)");
                return;
            }

            if (warningManager.updateWarningReason(id, newReason)) {
                e.replySuccess("Edited warning!");
            } else {
                e.replyError("Error while editing warning!");
            }
        } else {
            e.replyError(C.permMsg(Roles.HELPER));
        }
    }
}
