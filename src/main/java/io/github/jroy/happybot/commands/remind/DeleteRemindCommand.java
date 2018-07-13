package io.github.jroy.happybot.commands.remind;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.sql.timed.EventManager;
import io.github.jroy.happybot.util.Roles;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;

public class DeleteRemindCommand extends CommandBase {

    private final EventManager eventManager;

    public DeleteRemindCommand(EventManager eventManager) {
        super("deletereminder", "<id>", "Deletes a reminder.", CommandCategory.FUN);
        this.aliases = new String[]{"deleteremind", "dreminder", "dremind"};
        this.eventManager = eventManager;
    }

    @Override
    protected void executeCommand(CommandEvent e) {
        if (e.getSplitArgs().length >= 1 && StringUtils.isNumeric(e.getSplitArgs()[0])) {
            int id = Integer.parseInt(e.getSplitArgs()[0]);

            if (!e.hasRole(Roles.MODERATOR)) {
                if (!eventManager.isValidIdPair(id, e.getMember().getUser().getId())) {
                    e.reply("Either that is not your reminder or that reminder id doesn't exist");
                    return;
                }
            } else {
                if (!eventManager.isValidId(id)) {
                    e.reply("Invalid Reminder ID!");
                    return;
                }
            }

            try {
                eventManager.deleteInfraction(id);
                e.replySuccess("Deleted reminder!");
            } catch (SQLException e1) {
                e.replyError("Error while deleting your reminder!");
            }
        } else {
            e.reply(invalid);
        }
    }
}
