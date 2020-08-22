package io.github.jroy.happybot.sql;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
public class MessageFactory {

  private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `messages` ( `id` INT(10) NOT NULL AUTO_INCREMENT , `type` VARCHAR(50) NOT NULL , `value` BINARY(255) NOT NULL , UNIQUE (`id`)) ENGINE = InnoDB;";
  private static final String INSERT_MESSAGE = "INSERT INTO `messages` (`type`, `value`) VALUES (?, ?)";
  private static final String DELETE_MESSAGE = "DELETE FROM `messages` WHERE id = ?;";
  private static final String SELECT_MESSAGES = "SELECT * FROM `messages` WHERE type = ? ORDER BY RAND();";
  private final Connection connection;
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
      log.error("Error while caching messages: " + e.getMessage());
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
    preparedStatement.setBytes(2, message.getBytes(StandardCharsets.UTF_8));
    preparedStatement.execute();
  }

  public void deleteMessage(int id) throws SQLException {
    PreparedStatement preparedStatement = connection.prepareStatement(DELETE_MESSAGE);
    preparedStatement.setInt(1, id);
    preparedStatement.executeUpdate();
  }

  private void refreshMessages() throws SQLException {
    for (MessageType type : MessageType.values()) {
      refreshMessages(type);
    }
  }

  private void refreshMessages(MessageType type) throws SQLException {
    List<String> messages = parseMessages(type);
    switch (type) {
      case JOIN: {
        joinMessages = messages;
        break;
      }
      case LEAVE: {
        leaveMessages = messages;
        break;
      }
      case WARN: {
        warningMessages = messages;
        break;
      }
      case LEVEL: {
        levelUpMessages = messages;
        break;
      }
      case UPDATE_END: {
        updateEndMessages = messages;
        break;
      }
      case UPDATE_START: {
        updateStartMessages = messages;
        break;
      }
    }
  }

  private List<String> parseMessages(MessageType messageType) throws SQLException {
    return new ArrayList<>(getIdList(messageType, 50).values());
  }

  public Map<Integer, String> getIdList(MessageType messageType) throws SQLException {
    return getIdList(messageType, -1);
  }

  public Map<Integer, String> getIdList(MessageType messageType, int limit) throws SQLException {
    PreparedStatement preparedStatement = connection.prepareStatement(SELECT_MESSAGES);
    preparedStatement.setString(1, messageType.toString());
    ResultSet set = preparedStatement.executeQuery();

    Map<Integer, String> msgs = new HashMap<>();
    int amount = 0;
    while (set.next() && (limit == -1 || amount != limit)) {
      msgs.put(set.getInt("id"), new String(set.getBytes("value"), StandardCharsets.UTF_8).replace("\0", "").trim());
      amount++;
    }
    return msgs;
  }

  public Map<Integer, Map<Integer, String>> getPaginatedList(MessageType messageType) throws SQLException {
    Map<Integer, Map<Integer, String>> pageList = new HashMap<>();
    Map<Integer, String> list = new HashMap<>();
    int page = 1;
    int elementCount = 0;
    for (Map.Entry<Integer, String> entry : getIdList(messageType).entrySet()) {
      if (elementCount >= 10) {
        pageList.put(page, list);
        list = new HashMap<>();
        elementCount = 0;
        page++;
      }
      list.put(entry.getKey(), entry.getValue());
      elementCount++;
    }
    return pageList;
  }

  @Nonnull
  public String getRawMessage(MessageType messageType) throws SQLException {
    String message = "";
    int maxSize = getTotals(messageType);
    if (maxSize == 0) {
      //We ran out of random messages, get a pool of 50 more.
      refreshMessages(messageType);
      maxSize = getTotals(messageType);
    }
    int index = random.nextInt(maxSize);
    switch (messageType) {
      case JOIN: {
        message = joinMessages.get(index);
        joinMessages.remove(index);
        break;
      }
      case LEAVE: {
        message = leaveMessages.get(index);
        leaveMessages.remove(index);
        break;
      }
      case WARN: {
        String msg = warningMessages.get(index);
        if (!msg.toLowerCase().contains("<user>")) {
          msg = "<user>: " + msg;
        }
        message = msg;
        warningMessages.remove(index);
        break;
      }
      case LEVEL: {
        message = levelUpMessages.get(index);
        levelUpMessages.remove(index);
        break;
      }
      case UPDATE_END: {
        message = updateEndMessages.get(index);
        updateEndMessages.remove(index);
        break;
      }
      case UPDATE_START: {
        message = updateStartMessages.get(index);
        updateStartMessages.remove(index);
        break;
      }
    }
    return message;
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

    private final String translation;

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
