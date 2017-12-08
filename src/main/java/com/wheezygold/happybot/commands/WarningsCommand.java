package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Roles;
import com.wheezygold.happybot.util.WarningManager;
import net.dv8tion.jda.core.entities.Member;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WarningsCommand extends Command {

    private WarningManager warningManager;

    public WarningsCommand(WarningManager warningManager) {
        this.name = "warnings";
        this.arguments = "<user>";
        this.help = "Warns the target user.";
        this.guildOnly = true;
        this.category = new Category("Staff Tools");
        this.warningManager = warningManager;
    }

    @Override
    protected void execute(CommandEvent e) {
        if (C.hasRole(e.getMember(), Roles.HELPER)) {
            if (!C.containsMention(e)) {
                e.replyError("**Correct Usage:** ^" + name + " **<user>**");
                return;
            }
            try {
                ResultSet resultSet =warningManager.fetchWarnings(C.getMentionedMember(e).getUser().getId());
                StringBuilder builder = new StringBuilder();
                Member targetM = C.getMentionedMember(e);
                builder.append(targetM.getEffectiveName()).append("'s Warnings\n");
                while (resultSet.next()) {
                    Member staffMem = C.getGuild().getMemberById(resultSet.getString("staffid"));
                    if (staffMem != null) {
                        builder.append("#").append(resultSet.getString("id")).append(" ").append(C.bold(staffMem.getUser().getName() + "#" + staffMem.getUser().getDiscriminator())).append(" - ").append(C.bold(resultSet.getString("reason"))).append("\n");
                    }
                }
                e.reply(builder.toString());
            } catch (SQLException e1) {
                e.replyError("Oof Error: " + e1.getMessage());
            }
        } else {
            C.permMsg(Roles.HELPER);
        }
    }
}
