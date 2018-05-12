package io.github.jroy.happybot.sql;

import io.github.jroy.happybot.util.Logger;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("FieldCanBeLocal")
public class MessageFactory {

    private final Connection connection;

    private final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `messages` ( `id` INT(10) NOT NULL AUTO_INCREMENT , `type` VARCHAR(50) NOT NULL , `value` VARCHAR(255) NOT NULL , UNIQUE (`id`)) ENGINE = InnoDB;";
    private final String INSERT_MESSAGE = "INSERT INTO `messages` (`type`, `value`) VALUES (?, ?)";
    private final String SELECT_MESSAGES = "SELECT * FROM `messages` WHERE type = ?;";

    private final Random random = new Random();
    private String[] joinMessages;
    private String[] leaveMessages;
    private String[] updateStartMessages;
    private String[] updateEndMessages;
    private String[] warningMessages;

    public MessageFactory(SQLManager sqlManager) {
        connection = sqlManager.getConnection();
        try {
            connection.createStatement().executeUpdate(CREATE_TABLE);
            joinMessages = parseMessages(MessageType.JOIN);
            leaveMessages = parseMessages(MessageType.LEAVE);
            updateStartMessages = parseMessages(MessageType.UPDATE_START);
            updateEndMessages = parseMessages(MessageType.UPDATE_END);
            warningMessages = parseMessages(MessageType.WARN);
        } catch (SQLException e) {
            Logger.error("Error while caching messages: " + e.getMessage());
        }
    }

    private String[] parseMessages(MessageType messageType) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_MESSAGES);
        preparedStatement.setString(1, messageType.toString());
        ResultSet set = preparedStatement.executeQuery();

        List<String> msgs = new ArrayList<>();

        while (set.next()) {
            msgs.add(set.getString("value"));
        }

        return msgs.toArray(new String[0]);

    }

    @Nonnull
    public String getRawMessage(MessageType messageType) {
        if (messageType == MessageType.JOIN)
            return joinMessages[random.nextInt(joinMessages.length)];
        if (messageType == MessageType.LEAVE)
            return leaveMessages[random.nextInt(leaveMessages.length)];
        if (messageType == MessageType.WARN)
            return warningMessages[random.nextInt(warningMessages.length)];
        if (messageType == MessageType.UPDATE_START)
            return updateStartMessages[random.nextInt(updateStartMessages.length)];
        if (messageType == MessageType.UPDATE_END)
            return updateEndMessages[random.nextInt(updateEndMessages.length)];
        return "";
    }

    public int getTotals(MessageType messageType) {
        if (messageType == MessageType.JOIN)
            return joinMessages.length;
        if (messageType == MessageType.LEAVE)
            return leaveMessages.length;
        if (messageType == MessageType.UPDATE_START)
            return updateStartMessages.length;
        if (messageType == MessageType.UPDATE_END)
            return updateEndMessages.length;
        if (messageType == MessageType.WARN)
            return warningMessages.length;
        return 0;
    }

    public enum MessageType {
        JOIN("join"),
        LEAVE("leave"),
        UPDATE_START("updatestart"),
        UPDATE_END("updateend"),
        WARN("warn");

        private String translation;

        MessageType(String translation) {
            this.translation = translation;
        }

        @Override
        public String toString() {
            return translation;
        }
    }

}
