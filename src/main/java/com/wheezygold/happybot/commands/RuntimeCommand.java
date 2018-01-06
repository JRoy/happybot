package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Roles;
import com.wheezygold.happybot.util.RuntimeEditor;
import org.apache.commons.lang3.StringUtils;

public class RuntimeCommand extends Command {

    public RuntimeCommand() {
        this.name = "runtime";
        this.help = "Edit variables in runtime.";
        this.arguments = "<selfGilds/evalOwner/pingIssueClose/filterAdvert/publicWarns/gambleMax>";
        this.category = new Category("Bot Management");
    }

    @Override
    protected void execute(CommandEvent e) {
        if (C.hasRole(e.getMember(), Roles.DEVELOPER)) {
            String[] args = e.getArgs().split("[ ]");
            if (args.length == 2) {
                String target = args[0];
                String value = args[1];
                if (target.equalsIgnoreCase("selfGilds") && (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))) {
                    RuntimeEditor.setAllowSelfGilds(Boolean.parseBoolean(value.toLowerCase()));
                    e.replySuccess(":gear: Updated value to **" + value.toLowerCase() + "**!");
                } else if (target.equalsIgnoreCase("evalOwner") && (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))) {
                    RuntimeEditor.setEvalOwnerOnly(Boolean.parseBoolean(value.toLowerCase()));
                    e.replySuccess(":gear: Updated value to **" + value.toLowerCase() + "**!");
                } else if (target.equalsIgnoreCase("pingIssueClose") && (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))) {
                    RuntimeEditor.setPingIssueClose(Boolean.parseBoolean(value.toLowerCase()));
                    e.replySuccess(":gear: Updated value to **" + value.toLowerCase() + "**!");
                } else if (target.equalsIgnoreCase("filterAdvert") && (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))) {
                    RuntimeEditor.setFilteringAdverts(Boolean.parseBoolean(value.toLowerCase()));
                    e.replySuccess(":gear: Updated value to **" + value.toLowerCase() + "**!");
                } else if (target.equalsIgnoreCase("publicWarns") && (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))) {
                    RuntimeEditor.setPermittingWarningExposement(Boolean.parseBoolean(value.toLowerCase()));
                    e.replySuccess(":gear: Updated value to **" + value.toLowerCase() + "**!");
                } else if (target.equalsIgnoreCase("gambleMax") && StringUtils.isNumeric(value)) {
                    RuntimeEditor.setGambleMax(Integer.parseInt(value));
                    e.replySuccess(":gear: Updated value to **" + value + "**!");
                } else {
                    e.replyError("**Correct Usage:** ^" + name + " " + arguments);
                }
            } else {
                e.replyError("**Correct Usage:** ^" + name + " " + arguments);
            }
        } else {
            e.replyError(C.permMsg(Roles.DEVELOPER));
        }
    }
}
