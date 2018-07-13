package io.github.jroy.happybot.commands.remind;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.sql.timed.EventManager;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RemindersCommand extends CommandBase {

    private final EventManager eventManager;

    public RemindersCommand(EventManager eventManager) {
        super("reminders", null, "Lists all your active reminders.", CommandCategory.FUN);
        this.eventManager = eventManager;
    }

    @Override
    protected void executeCommand(CommandEvent e) {
        String targetId = e.getMember().getUser().getId();
        if (e.containsMention() && e.hasRole(Roles.MODERATOR)) {
            e.getMessage().addReaction(":check:").queue();
            targetId = e.getMentionedMember().getUser().getId();
        }
        try {
            ResultSet reminders = eventManager.getUserReminders(targetId);
            StringBuilder builder = new StringBuilder().append("Reminders:\n");
            while (reminders.next()) {
                builder.append("- #").append(reminders.getInt("id")).append(" ").append(C.code(reminders.getString("reason"))).append("\n");
            }
            e.reply(builder.toString());
        } catch (SQLException e1) {
            e.reply("Oops Error!");
        }
    }
}
