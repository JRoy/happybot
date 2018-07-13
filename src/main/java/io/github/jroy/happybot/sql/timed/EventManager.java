package io.github.jroy.happybot.sql.timed;

import io.github.jroy.happybot.sql.SQLManager;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Channels;
import io.github.jroy.happybot.util.Logger;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.StatusChangeEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("FieldCanBeLocal")
public class EventManager extends ListenerAdapter {

    private Connection connection;

    private boolean reg = false;

    private final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `spammers` ( `id` INT(50) NOT NULL AUTO_INCREMENT , `target` VARCHAR(50) NOT NULL , `epoch` BIGINT(255) NOT NULL , `wait` BIGINT(255) NOT NULL , `type` VARCHAR(255) NOT NULL , `reason` VARCHAR(255) NOT NULL DEFAULT 'None' , PRIMARY KEY (`id`)) ENGINE = InnoDB;";
    private final String CREATE_EVENT = "INSERT INTO `spammers` (target, epoch, wait, type) VALUES (?, ?, ?, ?);";
    private final String CREATE_REMINDER = "INSERT INTO `spammers` (target, epoch, wait, type, reason) VALUES (?, ?, ?, ?, ?);";
    private final String SELECT_EVENT = "SELECT * FROM `spammers` WHERE target = ? AND type = ?;";
    private final String SELECT_EVENT_ID = "SELECT * FROM `spammers` WHERE id = ?;";
    private final String SELECT_EVENT_ID_USER = "SELECT * FROM `spammers` WHERE id = ? AND target = ?;";
    private final String SELECT_ALL = "SELECT * FROM `spammers`;";
    private final String DELETE_EVENT = "DELETE FROM `spammers` WHERE id = ?;";
    private final String UPDATE_REMINDER = "UPDATE `spammers` SET reason = ? WHERE id = ?;";

    public EventManager(SQLManager sqlManager) {
        connection = sqlManager.getConnection();
        try {
            connection.createStatement().executeUpdate(CREATE_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createInfraction(String userId, long waitTime, EventType type) throws SQLException {
        if (isPunished(userId, type)) {
            deleteInfraction(userId, type);
        }

        PreparedStatement statement = connection.prepareStatement(CREATE_EVENT);
        statement.setString(1, userId);
        statement.setLong(2, System.currentTimeMillis());
        statement.setLong(3, waitTime);
        statement.setString(4, type.toString());
        statement.execute();
    }

    public void createReminder(String userId, long waitTime, String reason) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(CREATE_REMINDER);
        statement.setString(1, userId);
        statement.setLong(2, System.currentTimeMillis());
        statement.setLong(3, waitTime);
        statement.setString(4, EventType.REMIND.toString());
        statement.setString(5, reason);
        statement.execute();
    }

    public ResultSet getUserReminders(String userId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(SELECT_EVENT);
        statement.setString(1, userId);
        statement.setString(2, EventType.REMIND.toString());
        return statement.executeQuery();
    }

    public boolean isValidId(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement(SELECT_EVENT_ID);
            statement.setInt(1, id);
            return statement.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isValidIdPair(int id, String userId) {
        try {
            PreparedStatement statement = connection.prepareStatement(SELECT_EVENT_ID_USER);
            statement.setInt(1, id);
            statement.setString(2, userId);
            return statement.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setReminderReason(int id, String newReason) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(UPDATE_REMINDER);
        statement.setString(1, newReason);
        statement.setInt(2, id);
        statement.executeUpdate();
    }

    public boolean isPunished(String userId, EventType eventType) {
        try {
            PreparedStatement statement = connection.prepareStatement(SELECT_EVENT);
            statement.setString(1, userId);
            statement.setString(2, eventType.toString());
            return statement.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void deleteInfraction(int id) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(DELETE_EVENT);
        statement.setInt(1, id);
        statement.execute();
    }

    public void deleteInfraction(String userId, EventType eventType) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(SELECT_EVENT);
        statement.setString(1, userId);
        statement.setString(2, eventType.toString());
        ResultSet set = statement.executeQuery();
        set.next();
        deleteInfraction(set.getInt("id"));
    }

    @Override
    public void onStatusChange(StatusChangeEvent event) {
        if (reg)
            return;
        if (event.getNewStatus() == JDA.Status.CONNECTED) {
            reg = true;
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        ResultSet resultSet = connection.createStatement().executeQuery(SELECT_ALL);
                        while (resultSet.next()) {
                            EventType eventType = EventType.getFromTranslation(resultSet.getString("type"));
                            if (eventType == null)
                                continue;
                            int id = resultSet.getInt("id");
                            String userId = resultSet.getString("target");
                            long createdEpoch = resultSet.getLong("epoch");
                            long waitTime = resultSet.getLong("wait");
                            String reason = resultSet.getString("reason");

                            Member targetMember = C.getGuild().getMemberById(userId);
                            if (targetMember == null) {
                                deleteInfraction(id);
                                continue;
                            }

                            if ((System.currentTimeMillis() - createdEpoch) < waitTime) {
                                continue;
                            }

                            switch (eventType) {
                                case XP: {
                                    C.removeRole(targetMember, Roles.EXP_SPAMMER);
                                    deleteInfraction(id);
                                    C.privChannel(targetMember, "Your EXP-Spammer has been removed!");
                                    break;
                                }
                                case MUTE: {
                                    C.removeRole(targetMember, Roles.MUTED);
                                    deleteInfraction(id);
                                    C.privChannel(targetMember, "You are no longer muted!");
                                    break;
                                }
                                case REMIND: {
                                    deleteInfraction(id);
                                    Channels.RANDOM.getChannel().sendMessage(":bell: :bell: " + targetMember.getAsMention() + ": New Reminder! :bell: :bell:" + C.codeblock(reason)).queue();
                                    break;
                                }
                                default:
                                    break;
                            }
                        }
                    } catch (SQLException e) {
                        Logger.error("Error while timing!");
                        e.printStackTrace();
                    }
                }
            }, 0, 5000);
        }
    }
}
