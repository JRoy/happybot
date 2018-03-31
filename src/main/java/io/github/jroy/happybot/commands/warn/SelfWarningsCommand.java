package io.github.jroy.happybot.commands.warn;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.sql.WarningManager;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SelfWarningsCommand extends Command {

    private WarningManager warningManager;

    public SelfWarningsCommand(WarningManager warningManager) {
        this.name = "mywarns";
        this.aliases = new String[]{"mywarnings"};
        this.help = "Lists your warnings.";
        this.guildOnly = false;
        this.category = new Category("General");
        this.warningManager = warningManager;
    }

    @Override
    protected void execute(CommandEvent e) {
        e.reply("Providing Memes in DM's");
        try {
            ResultSet resultSet = warningManager.fetchWarnings(e.getEvent().getAuthor().getId());
            StringBuilder builder = new StringBuilder();
            User targetM = e.getEvent().getAuthor();
            builder.append(targetM.getName()).append("'s Warnings\n");
            while (resultSet.next()) {
                Member staffMem = C.getGuild().getMemberById(resultSet.getString("staffid"));
                if (staffMem != null) {
                    builder.append("#").append(resultSet.getString("id")).append(" ").append(C.bold(staffMem.getUser().getName() + "#" + staffMem.getUser().getDiscriminator())).append(" - ").append(C.bold(resultSet.getString("reason"))).append("\n");
                }
            }
            e.replyInDm(builder.toString());
        } catch (SQLException e1) {
            e.replyError("Oof Error: " + e1.getMessage());
        }
    }
}
