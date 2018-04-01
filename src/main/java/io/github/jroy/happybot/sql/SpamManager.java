package io.github.jroy.happybot.sql;

import io.github.jroy.happybot.util.C;
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
public class SpamManager extends ListenerAdapter {

    private Connection connection;

    private boolean reg = false;

    private final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `spammers` ( `id` INT(50) NOT NULL AUTO_INCREMENT , `target` VARCHAR(50) NOT NULL , `epoch` BIGINT(255) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;";
    private final String CREATE_EVENT = "INSERT INTO `spammers` (target, epoch) VALUES (?, ?);";
    private final String SELECT_EVENT = "SELECT * FROM `spammers` WHERE target = ?;";
    private final String SELECT_ALL = "SELECT * FROM `spammers`;";
    private final String DELETE_EVENT = "DELETE FROM `spammers` WHERE target = ?;";

    public SpamManager(SQLManager sqlManager) {
        connection = sqlManager.getConnection();
        try {
            connection.createStatement().executeUpdate(CREATE_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean createInfraction(String userId) throws SQLException {
        if (isPunished(userId)) {
            deleteInfraction(userId);
        }
        PreparedStatement statement = connection.prepareStatement(CREATE_EVENT);
        statement.setString(1, userId);
        statement.setLong(2, System.currentTimeMillis());
        return statement.execute();
    }

    public boolean isPunished(String userId) {
        try {
            PreparedStatement statement = connection.prepareStatement(SELECT_EVENT);
            statement.setString(1, userId);
            return statement.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public long getEpochFromUser(String userId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(SELECT_EVENT);
        statement.setString(1, userId);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getLong("epoch");
    }

    public void deleteInfraction(String userId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(DELETE_EVENT);
        statement.setString(1, userId);
        statement.execute();
    }

    @Override
    public void onStatusChange(StatusChangeEvent event) {
        if (reg)
            return;
        if (event.getStatus() == JDA.Status.CONNECTED) {
            reg = true;
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        ResultSet resultSet = connection.createStatement().executeQuery(SELECT_ALL);
                        while (resultSet.next()) {
                            String user = resultSet.getString("target");
                            if (C.getGuild().getMemberById(user) == null) {
                                deleteInfraction(user);
                                continue;
                            }
                            long epoch = resultSet.getLong("epoch");
                            if ((System.currentTimeMillis() - epoch) >= 604800000) {
                                Member m = C.getGuild().getMemberById(user);
                                C.removeRole(m, Roles.EXP_SPAMMER);
                                deleteInfraction(user);
                                C.privChannel(m, "Your EXP-Spammer has been removed!");
                            }
                        }
                    } catch (SQLException e) {
                        Logger.error("Error while timing!");
                        e.printStackTrace();
                    }
                }
            }, 0, 300000);
        }
    }
}
