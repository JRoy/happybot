package io.github.jroy.happybot.events.star;

import io.github.jroy.happybot.sql.SQLManager;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Channels;
import io.github.jroy.happybot.util.Logger;
import io.github.jroy.happybot.util.Roles;
import io.github.jroy.happybot.util.RuntimeEditor;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("FieldCanBeLocal")
public class StarMessages extends ListenerAdapter {
  private static final String NEW_GILDED_MESSAGE = "New Gilded Message";

  /**
   * This Table Stores Statistics Related to the Amount of Starred, Heeled, and Gilded Messages a User Has
   * <p>
   * `id`: INT
   * `userid`: VARCHAR (String) - The id of the user
   * `stars`: BIGINT (Long) - The amount of stars the user has
   * `gilds`: BIGINT (Long) - The amount of gilds the user has
   * `heels`: BIGINT (Long) - The amount of heels the user has
   */
  private final String CREATE_STAT_TABLE = "CREATE TABLE IF NOT EXISTS starstats ( `id` INT(50) NOT NULL AUTO_INCREMENT , `userid` VARCHAR(50) NOT NULL , `stars` BIGINT(255) NOT NULL DEFAULT '0' , `gilds` BIGINT(255) NOT NULL DEFAULT '0' , `heels` BIGINT(255) NULL DEFAULT '0' , PRIMARY KEY (`id`)) ENGINE = InnoDB;";
  private final String SELECT_USER = "SELECT * FROM starstats WHERE userid = ?;";
  private final String CREATE_USER = "INSERT INTO starstats (userId) VALUES (?);";
  private final String UPDATE_USER = "UPDATE starstats SET stars = ?, gilds = ?, heels = ? WHERE userId = ?;";

  /**
   * This Table Stores a Record of what Messages have been starred/heeled before in order to prevent duplicates arising with bot restarts
   * <p>
   * `id`: INT
   * `messageid`: VARCHAR (String) - The id of the message which has been starred/gilded
   */
  private final String CREATE_USED_TABLE = "CREATE TABLE IF NOT EXISTS starused ( `id` INT(50) NOT NULL AUTO_INCREMENT , `messageId` VARCHAR(255) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;";
  private final String ADD_USED = "INSERT INTO starused (messageId) VALUES (?);";
  private final String SELECT_USED = "SELECT * FROM starused WHERE messageId = ?;";

  /**
   * This Table Stores the Amount of Stars a Message Will Take Before Sent to the #starred-messages Channel
   * - This is not required for all messages, only those you wish to alter the default for
   * <p>
   * `id`: INT
   * `messageid`: VARCHAR (String) - The id of the message to be modified
   * `amount`: INT - The amount of stars required to send the target message to #starred-messages
   */
  private final String CREATE_CUSTOM_TABLE = "CREATE TABLE IF NOT EXISTS starcounts ( `id` INT(50) NOT NULL AUTO_INCREMENT , `messageid` VARCHAR(255) NOT NULL , `amount` INT(50) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;";
  private final String ADD_CUSTOM = "INSERT INTO starcounts (messageid, amount) VALUES (?, ?);";
  private final String SELECT_CUSTOM = "SELECT * FROM starcounts WHERE messageid = ?;";
  private final String SELECT_ALL_CUSTOM = "SELECT * FROM starcounts";
  private final String DELETE_CUSTOM = "DELETE FROM starcounts WHERE messageid = ?;";

  private Connection connection;
  private Set<String> alreadyUsedMessages = new HashSet<>();
  private Map<String, GildInfoToken> pastGilds = new HashMap<>();
  private Map<String, Integer> alteredMessages = new HashMap<>();

  public StarMessages(SQLManager sqlManager) {
    connection = sqlManager.getConnection();
    try {
      connection.createStatement().executeUpdate(CREATE_STAT_TABLE);
      connection.createStatement().executeUpdate(CREATE_USED_TABLE);
      connection.createStatement().executeUpdate(CREATE_CUSTOM_TABLE);
      ResultSet set = connection.createStatement().executeQuery(SELECT_ALL_CUSTOM);
      while (set.next()) {
        alteredMessages.put(set.getString("messageid"), set.getInt("amount"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Takes note of a requested alteration in the default star count
   *
   * @param messageId     The id of the message being altered
   * @param requiredStars The new amount of stars required.
   */
  public void addStarAlter(String messageId, int requiredStars) {
    try {
      PreparedStatement statement = connection.prepareStatement(ADD_CUSTOM);
      statement.setString(1, messageId);
      statement.setInt(2, requiredStars);
      statement.executeUpdate();
      alteredMessages.put(messageId, requiredStars);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Removes star alteration from a message
   *
   * @param messageId The message id to be removed
   */
  public void deleteStarAlter(String messageId) {
    try {
      PreparedStatement statement = connection.prepareStatement(DELETE_CUSTOM);
      statement.setString(1, messageId);
      statement.executeUpdate();
      alteredMessages.remove(messageId);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Checks if a message has an alteration on it
   *
   * @param messageId The id of the message to be checked
   * @return Returns true if the message has an alteration
   */
  public boolean isAltered(String messageId) {
    return alteredMessages.containsKey(messageId);
  }

  /**
   * Adds a message to the SQL records
   *
   * @param messageId The id of the message
   */
  private void addUsed(String messageId) {
    try {
      PreparedStatement statement = connection.prepareStatement(ADD_USED);
      statement.setString(1, messageId);
      statement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Checks if a message has already been starred/heeled on a previous restart
   *
   * @param messageId The id of the message to be checked
   * @return Returns true if the message has been starred/heeled before
   */
  private boolean isUsed(String messageId) {
    try {
      PreparedStatement statement = connection.prepareStatement(SELECT_USED);
      statement.setString(1, messageId);
      return statement.executeQuery().next();
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Gets the stats of a user and places them in a {@link io.github.jroy.happybot.events.star.StarStatsToken token} which adds ease-of-use methods.
   *
   * @param userId The id of the user being checked
   * @return The {@link io.github.jroy.happybot.events.star.StarStatsToken StarStatsToken} of the user.
   */
  public StarStatsToken getUser(String userId) {
    try {
      if (!isPropagated(userId)) {
        createUser(userId);
      }
      PreparedStatement statement = connection.prepareStatement(SELECT_USER);
      statement.setString(1, userId);
      return new StarStatsToken(statement.executeQuery());
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Checks to see if a user has had their messages starred/heeled/gilded before.
   *
   * @param userId The id of the user to check.
   * @return Returns true if the user has been starred/heeled/gilded before.
   */
  private boolean isPropagated(String userId) {
    try {
      PreparedStatement statement = connection.prepareStatement(SELECT_USER);
      statement.setString(1, userId);
      return statement.executeQuery().next();
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Propagates a user's stats in sql.
   *
   * @param userId The id of the user to propagate.
   */
  private void createUser(String userId) {
    try {
      PreparedStatement statement = connection.prepareStatement(CREATE_USER);
      statement.setString(1, userId);
      statement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Adds one star to the user's statistics.
   *
   * @param userId The id of the user to add to.
   */
  private void addStar(String userId) {
    try {
      StarStatsToken token = getUser(userId);
      PreparedStatement statement = connection.prepareStatement(UPDATE_USER);
      statement.setLong(1, token.getStarCount() + 1);
      statement.setLong(2, token.getGildCount());
      statement.setLong(3, token.getHeelCount());
      statement.setString(4, userId);
      statement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Adds one gild to the user's statistics.
   *
   * @param userId The id of the user to add to.
   */
  private void addGild(String userId, int amount) {
    try {
      StarStatsToken token = getUser(userId);
      PreparedStatement statement = connection.prepareStatement(UPDATE_USER);
      statement.setLong(1, token.getStarCount());
      statement.setLong(2, token.getGildCount() + amount);
      statement.setLong(3, token.getHeelCount());
      statement.setString(4, userId);
      statement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Adds one heel to the user's statistics.
   *
   * @param userId The id of the user to add to.
   */
  private void addHeel(String userId) {
    try {
      StarStatsToken token = getUser(userId);
      PreparedStatement statement = connection.prepareStatement(UPDATE_USER);
      statement.setLong(1, token.getStarCount());
      statement.setLong(2, token.getGildCount());
      statement.setLong(3, token.getHeelCount() + 1);
      statement.setString(4, userId);
      statement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
    String reactionName = e.getReactionEmote().getName();
    StarEmote emote = StarEmote.getByName(reactionName);
    if (emote == null && "gild".equals(reactionName)) {
      if (!C.hasRole(e.getMember(), Roles.MODERATOR)) {
        e.getReaction().removeReaction().queue();
        return;
      }
      e.getChannel().getMessageById(e.getMessageId()).queue(message -> {
        if (!RuntimeEditor.isAllowSelfGilds() && e.getMember().getUser() == message.getAuthor()) {
          e.getReaction().removeReaction().queue();
          return;
        }
        handleGild(e);
      });
    } else {
      handleStar(e, emote);
    }
  }

  @Override
  public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent e) {
    if (e.getReactionEmote().getName().equals("gild") && pastGilds.containsKey(e.getMessageId()) && pastGilds.get(e.getMessageId()).getGilderId().equals(e.getUser().getId())) {
      for (Message msg : pastGilds.get(e.getMessageId()).getCausedMessages()) {
        msg.delete().queue();
      }
      addGild(pastGilds.get(e.getMessageId()).getTargetId(), -1);
      pastGilds.remove(e.getMessageId());
      alreadyUsedMessages.remove(e.getMessageId());
    }
  }

  private void handleStar(GuildMessageReactionAddEvent e, StarEmote emote) {
    CompletableFuture.runAsync(new HandleStar(e, emote));
  }

  private void handleGild(GuildMessageReactionAddEvent e) {
    CompletableFuture.runAsync(new HandleGild(e));
  }

  private void sendStarredMessage(String footer, Message message, String privateMessageText, StarEmote emote, Member causedUser) {
    EmbedBuilder embed = new EmbedBuilder()
        .setTitle(C.getFullName(message.getAuthor()))
        .setDescription(message.getContentRaw());

    if (message.getEmbeds().size() > 0) {
      message.getChannel().sendMessage("Failed to Star/Gild/Shoe a Message: Contained an un-readable embed! Cannot Continue!").queue();
      return;
    }

    GildInfoToken infoToken = new GildInfoToken(causedUser.getUser().getId(), message.getAuthor().getId());

    if (footer.startsWith(NEW_GILDED_MESSAGE)) {
      embed.setFooter(footer, "https://cdn.discordapp.com/emojis/371121885997694976.png?v=1");
      pastGilds.put(message.getId(), infoToken);
    } else {
      embed.setFooter(footer, emote.getIconUrl());
    }

    embed.setThumbnail(message.getMember().getUser().getAvatarUrl())
        .setColor(message.getMember().getColor());
    if (C.containsImage(message)) {
      embed.setImage(C.getImage(message));
    }

    infoToken.addCaused(Channels.STARRED_MESSAGES.getChannel().sendMessage(embed.build()).complete());
    C.privChannel(message.getMember(), privateMessageText);
    C.privChannel(message.getMember(), embed.build());
    alreadyUsedMessages.add(message.getId());
  }

  private class HandleStar implements Runnable {

    private static final int NUM_STARS_REQUIRED = 5;
    private GuildMessageReactionAddEvent e;
    private Message message;
    private StarEmote emote;

    HandleStar(GuildMessageReactionAddEvent e, StarEmote emote) {
      this.e = e;
      this.emote = emote;
      message = e.getChannel().getMessageById(e.getMessageId()).complete();
    }

    @Override
    public void run() {
      if (e.getChannel().getId().equals(
          Channels.STARRED_MESSAGES.getId()) || e.getChannel().getId().equals(
          Channels.BOT_META.getId()) || e.getChannel().getId().equals(
          Channels.TWITTER.getId()) || e.getChannel().getId().equals(Channels.LIVE.getId())) {
        return;
      }

      try {
        // total number of stars and shoes combined
        int numberOfStars = message.getReactions().stream()
            .filter(reaction -> StarEmote.getByName(reaction.getReactionEmote().getName()) != null)
            .mapToInt(MessageReaction::getCount)
            .sum();

        if (numberOfStars >= NUM_STARS_REQUIRED && !alreadyUsedMessages.contains(message.getId())) {
          if (isUsed(message.getId())) {
            alreadyUsedMessages.add(message.getId());
            return;
          }

          if (isAltered(message.getId()) && numberOfStars < alteredMessages.get(message.getId())) {
            return;
          }

          String footer = "New " + emote.getAction() + " message from #" + message.getChannel().getName() + " • " + numberOfStars + emote.getName();
          String privateMessageText = "Congrats! One of your messages has been " + emote.getAction() + ":";
          sendStarredMessage(footer, message, privateMessageText, emote, e.getMember());
          switch (emote) {
            case HEEL:
              addHeel(message.getAuthor().getId());
              break;
            case STAR:
              addStar(message.getAuthor().getId());
              break;
          }
          //This should only be used for heels and stars
          addUsed(message.getId());
        }
      } catch (NullPointerException | IllegalStateException e) {
        Logger.error("Star reaction is in invalid state!");
      }
    }
  }

  private class HandleGild implements Runnable {

    private GuildMessageReactionAddEvent e;
    private Message message;

    HandleGild(GuildMessageReactionAddEvent e) {
      this.e = e;
      message = e.getChannel().getMessageById(e.getMessageId()).complete();
    }

    @Override
    public void run() {
      if (!alreadyUsedMessages.contains(message.getId())) {
        String footer = NEW_GILDED_MESSAGE + " from #" + message.getChannel().getName() + " (" + C.getFullName(e.getUser()) + ")";
        String privateMessageText = "Congrats! One of your messages has been gilded by a staff member:";
        alreadyUsedMessages.add(message.getId());
        sendStarredMessage(footer, message, privateMessageText, null, e.getMember());
        addGild(message.getAuthor().getId(), 1);
      }
    }
  }
}
