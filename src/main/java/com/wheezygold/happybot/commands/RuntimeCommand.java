package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Roles;
import com.wheezygold.happybot.util.RuntimeEditor;
import org.apache.commons.lang3.StringUtils;

public class RuntimeCommand extends Command {

    public RuntimeCommand() {
        this.name = "runtime";
        this.help = "Edit variables in runtime.";
        this.arguments = "<selfGilds/evalOwner/pingIssueClose/filterAdvert/publicWarns/editUserWarns/gambleMax>";
        this.category = new Category("Bot Management");
    }

    @Override
    protected void execute(CommandEvent e) {
        if (C.hasRole(e.getMember(), Roles.DEVELOPER)) {
            String[] args = e.getArgs().split("[ ]");
            if (args.length == 2) {
                String target = args[0];
                String value = args[1];
                boolean valid = true;
                switch (target) {
                    case "selfGilds": {
                        if (C.isBool(value)){
                            RuntimeEditor.setAllowSelfGilds(Boolean.parseBoolean(value.toLowerCase()));
                        } else {
                            valid = false;
                        }
                        break;
                    }
                    case "evalOwner": {
                        if (C.isBool(value)) {
                            RuntimeEditor.setEvalOwnerOnly(Boolean.parseBoolean(value.toLowerCase()));
                        } else {
                            valid = false;
                        }
                        break;
                    }
                    case "pingIssueClose": {
                        if (C.isBool(value)) {
                            RuntimeEditor.setPingIssueClose(Boolean.parseBoolean(value.toLowerCase()));
                        } else {
                            valid = false;
                        }
                        break;
                    }
                    case "filterAdvert": {
                        if (C.isBool(value)) {
                            RuntimeEditor.setFilteringAdverts(Boolean.parseBoolean(value.toLowerCase()));
                        } else {
                            valid = false;
                        }
                        break;
                    }
                    case "editUserWarns": {
                        if (C.isBool(value)) {
                            RuntimeEditor.setAllowEditOtherUserWarn(Boolean.parseBoolean(value.toLowerCase()));
                        } else {
                            valid = false;
                        }
                        break;
                    }
                    case "publicWarns": {
                        if (C.isBool(value)) {
                            RuntimeEditor.setPermittingWarningExposement(Boolean.parseBoolean(value.toLowerCase()));
                        } else {
                            valid = false;
                        }
                        break;
                    }
                    case "gambleMax": {
                        if (StringUtils.isNumeric(value)) {
                            RuntimeEditor.setGambleMax(Integer.parseInt(value));
                        } else {
                            valid = false;
                        }
                        break;
                    }
                    default: {
                        e.replyError("**Correct Usage:** ^" + name + " " + arguments);
                        return;
                    }
                }
                if (valid) {
                    e.replySuccess(":gear: Updated value to **" + value.toLowerCase() + "**!");
                    return;
                }
                e.replyError("**Correct Usage:** ^" + name + " " + arguments);
            } else {
                e.replyError("**Correct Usage:** ^" + name + " " + arguments);
            }
        } else {
            e.replyError(C.permMsg(Roles.DEVELOPER));
        }
    }
}
