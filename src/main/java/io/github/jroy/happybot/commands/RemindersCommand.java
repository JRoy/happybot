package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.sql.timed.EventManager;
import io.github.jroy.happybot.util.C;

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
        try {
            ResultSet reminders = eventManager.getUserReminders(e.getMember().getUser().getId());
            StringBuilder builder = new StringBuilder().append("Your Reminders:\n");
            while (reminders.next()) {
                builder.append("- ").append(C.code(reminders.getString("reason")));
            }
            e.reply(builder.toString());
        } catch (SQLException e1) {
            e.reply("Oops Error!");
        }
    }
}
