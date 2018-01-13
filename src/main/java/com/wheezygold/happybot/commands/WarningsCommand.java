package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Roles;
import com.wheezygold.happybot.util.RuntimeEditor;
import com.wheezygold.happybot.sql.WarningManager;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WarningsCommand extends Command {

    private WarningManager warningManager;

    public WarningsCommand(WarningManager warningManager) {
        this.name = "warnings";
        this.aliases = new String[]{"warns"};
        this.arguments = "<user>";
        this.help = "Warns the target user.";
        this.guildOnly = true;
        this.category = new Category("Staff Tools");
        this.warningManager = warningManager;
    }

    @Override
    protected void execute(CommandEvent e) {
        if (C.hasRole(e.getMember(), Roles.HELPER) || RuntimeEditor.isPermittingWarningExposement()) {
            if (!C.containsMention(e)) {
                try {
                    WarningToken token = grabWarnings(e.getAuthor());
                    if (token.getWarnings() == 0) {
                        e.replyError("**Correct Usage:** ^" + name + " **<user>**");
                        return;
                    }
                    e.reply(token.getBuilder().toString());
                    return;
                } catch (SQLException e1) {
                    e.replyError("**Correct Usage:** ^" + name + " **<user>**");
                }
                e.replyError("**Correct Usage:** ^" + name + " **<user>**");
                return;
            }
            try {
                WarningToken token = grabWarnings(C.getMentionedMember(e).getUser());
                if (token.getWarnings() == 0) {
                    e.replyError("Target User has no warnings.");
                    return;
                }
                e.reply(token.getBuilder().toString());
            } catch (SQLException e1) {
                e.replyError("Oof Error: " + e1.getMessage());
            }
        } else {
            e.reply(C.permMsg(Roles.HELPER));
        }
    }

    private WarningToken grabWarnings(User user) throws SQLException {
        ResultSet resultSet = warningManager.fetchWarnings(user.getId());
        StringBuilder builder = new StringBuilder();
        builder.append(user.getName()).append("'s Warnings\n");
        int warnings = 0;
        while (resultSet.next()) {
            Member staffMem = C.getGuild().getMemberById(resultSet.getString("staffid"));
            if (staffMem != null) {
                builder.append("#").append(resultSet.getString("id")).append(" ").append(C.bold(staffMem.getUser().getName() + "#" + staffMem.getUser().getDiscriminator())).append(" - ").append(C.bold(resultSet.getString("reason"))).append("\n");
                warnings++;
            }
        }
        return new WarningToken(builder, warnings);
    }

    private class WarningToken {

        private StringBuilder builder;
        private int warnings;

        WarningToken(StringBuilder builder, int warnings) {
            this.builder = builder;
            this.warnings = warnings;
        }

        private StringBuilder getBuilder() {
            return builder;
        }

        private int getWarnings() {
            return warnings;
        }
    }

}
