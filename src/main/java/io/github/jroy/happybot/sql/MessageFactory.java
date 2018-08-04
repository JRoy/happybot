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
  private List<String> joinMessages = new ArrayList<>();
  private List<String> leaveMessages = new ArrayList<>();
  private List<String> updateStartMessages = new ArrayList<>();
  private List<String> updateEndMessages = new ArrayList<>();
  private List<String> warningMessages = new ArrayList<>();
  private List<String> levelUpMessages = new ArrayList<>();

  public MessageFactory(SQLManager sqlManager) {
    connection = sqlManager.getConnection();
    try {
      connection.createStatement().executeUpdate(CREATE_TABLE);
      refreshMessages();
    } catch (SQLException e) {
      Logger.error("Error while caching messages: " + e.getMessage());
    }
  }
  
  public void addMessage(MessageType messageType, String message) throws SQLException {
    message = message.replace("(user)", "<user>").replace("[user]", "<user>").replace("{user}", "<user>");
    switch (messageType) { //We add the message to the cache first; we don't want to call SQL every time.
      case JOIN: {
        if (!joinMessages.contains(message)) {
          joinMessages.add(message);
        }
        break;
      }
      case WARN: {
        if (!message.toLowerCase().contains("<user>")) {
          message = "<user>: " + message;
        }
        if (!warningMessages.contains(message)) {
          warningMessages.add(message);
        }
        break;
      }
      case LEAVE: {
        if (!leaveMessages.contains(message)) {
          leaveMessages.add(message);
       }
        break;
      }
      case UPDATE_END: {
        if (!updateEndMessages.contains(message)) {
          updateEndMessages.add(message);
        }
        break;
      }
      case UPDATE_START: {
        if (!updateStartMessages.contains(message)) {
          updateStartMessages.add(message);
        }
        break;
      }
      case LEVEL: {
        if (!levelUpMessages.contains(message)) {
          levelUpMessages.add(message);
        }
        break;
      }
      default:
        break; //the hell
    }
    PreparedStatement preparedStatement = connection.prepareStatement(INSERT_MESSAGE);
    preparedStatement.setString(1, messageType.toString());
    preparedStatement.setString(2, message);
    preparedStatement.execute();
  }

  private void refreshMessages() throws SQLException {
    joinMessages.clear();
    leaveMessages.clear();
    updateStartMessages.clear();
    updateEndMessages.clear();
    warningMessages.clear();
    levelUpMessages.clear();
    joinMessages = parseMessages(MessageType.JOIN);
    leaveMessages = parseMessages(MessageType.LEAVE);
    updateStartMessages = parseMessages(MessageType.UPDATE_START);
    updateEndMessages = parseMessages(MessageType.UPDATE_END);
    warningMessages = parseMessages(MessageType.WARN);
    levelUpMessages = parseMessages(MessageType.LEVEL);
  }

  private List<String> parseMessages(MessageType messageType) throws SQLException {
    PreparedStatement preparedStatement = connection.prepareStatement(SELECT_MESSAGES);
    preparedStatement.setString(1, messageType.toString());
    ResultSet set = preparedStatement.executeQuery();

    List<String> msgs = new ArrayList<>();
    while (set.next()) {
      msgs.add(set.getString("value"));
    }
    return msgs;
  }

  @Nonnull
  public String getRawMessage(MessageType messageType) {
    if (messageType == MessageType.JOIN) {
      return joinMessages.get(random.nextInt(joinMessages.size()));
    }
    if (messageType == MessageType.LEAVE) {
      return leaveMessages.get(random.nextInt(leaveMessages.size()));
    }
    if (messageType == MessageType.WARN) {
      String message = warningMessages.get(random.nextInt(warningMessages.size()));
      if (!message.toLowerCase().contains("<user>")) {
        message = "<user>: " + message;
      }
      return message;
    }
    if (messageType == MessageType.UPDATE_START) {
      return updateStartMessages.get(random.nextInt(updateStartMessages.size()));
    }
    if (messageType == MessageType.UPDATE_END) {
      return updateEndMessages.get(random.nextInt(updateEndMessages.size()));
    }
    if (messageType == MessageType.LEVEL) {
      return levelUpMessages.get(random.nextInt(levelUpMessages.size()));
    }
    return "";
  }

  public int getTotals(MessageType messageType) {
    if (messageType == MessageType.JOIN) {
      return joinMessages.size();
    }
    if (messageType == MessageType.LEAVE) {
      return leaveMessages.size();
    }
    if (messageType == MessageType.UPDATE_START) {
      return updateStartMessages.size();
    }
    if (messageType == MessageType.UPDATE_END) {
      return updateEndMessages.size();
    }
    if (messageType == MessageType.WARN) {
      return warningMessages.size();
    }
    if (messageType == MessageType.LEVEL) {
      return levelUpMessages.size();
    }
    return 0;
  }

  public enum MessageType {
    JOIN("join"),
    LEAVE("leave"),
    UPDATE_START("updatestart"),
    UPDATE_END("updateend"),
    WARN("warn"),
    LEVEL("level");

    private String translation;

    MessageType(String translation) {
      this.translation = translation;
    }

    public static MessageType fromText(String typeText) {
      for (MessageType messageType : MessageType.values()) {
        if (messageType.toString().equalsIgnoreCase(typeText)) {
          return messageType;
        }
      }
      return null;
    }

    public static String getTypes(String join) {
      List<String> types = new ArrayList<>();
      for (MessageType messageType : MessageType.values()) {
        types.add(messageType.translation);
      }
      return String.join(join + " ", types);
    }

    @Override
    public String toString() {
      return translation;
    }

  }

}
