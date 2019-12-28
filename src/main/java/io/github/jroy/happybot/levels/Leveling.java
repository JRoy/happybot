package io.github.jroy.happybot.levels;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.udojava.evalex.Expression;
import io.github.jroy.happybot.sql.MessageFactory;
import io.github.jroy.happybot.sql.PurchaseManager;
import io.github.jroy.happybot.sql.Reward;
import io.github.jroy.happybot.sql.SQLManager;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Channels;
import io.github.jroy.happybot.util.Roles;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.StatusChangeEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("FieldCanBeLocal")
@Slf4j
public class Leveling extends ListenerAdapter {

  private final static int MAX_LEVEL = 200;
  private final Connection connection;
  private final MessageFactory messageFactory;
  private final PurchaseManager purchaseManager;
  private final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `levels` ( `id` INT NOT NULL AUTO_INCREMENT , `userId` VARCHAR(255) NOT NULL , `level` BIGINT(255) NOT NULL DEFAULT '0' , UNIQUE (`id`)) ENGINE = InnoDB;";
  private final String SELECT_USER = "SELECT * FROM `levels` WHERE userId = ?;";
  private final String CREATE_USER = "INSERT INTO `levels` (userId) VALUES (?);";
  private final String UPDATE_USER = "UPDATE `levels` SET level = ? WHERE userId = ?;";
  private final String CREATE_IMG_TABLE = "CREATE TABLE IF NOT EXISTS `backgrounds` ( `id` INT(255) NOT NULL AUTO_INCREMENT , `userid` VARCHAR(255) , `img` LONGBLOB , UNIQUE (`id`)) ENGINE = InnoDB;";
  private final String INSERT_IMAGE = "INSERT INTO `backgrounds` (userid, img) VALUES (?, ?);";
  private final String UPDATE_IMAGE = "UPDATE `backgrounds` SET img = ? WHERE userid = ?;";
  private final String SELECT_IMAGE = "SELECT * FROM `backgrounds` WHERE userid = ?;";
  private final String leaderboardMessageId = "475436792980701195";
  private final Random random = new Random();
  public Map<Integer, LevelingToken> topCache = new HashMap<>();
  private boolean registered = false;
  private TreeMap<Long, Integer> levels = new TreeMap<>();
  private Map<String, OffsetDateTime> lastChatTimes = new HashMap<>();
  private LoadingCache<String, Integer> levelCache = CacheBuilder.newBuilder()
      .expireAfterAccess(10, TimeUnit.MINUTES)
      .maximumSize(200)
      .build(new CacheLoader<String, Integer>() {
        @Override
        public Integer load(@NotNull String key) {
          return toLevel(getExp(key));
        }
      });

  public Leveling(SQLManager sqlManager, MessageFactory messageFactory, PurchaseManager purchaseManager) {
    this.connection = sqlManager.getConnection();
    this.messageFactory = messageFactory;
    this.purchaseManager = purchaseManager;
    try {
      connection.createStatement().executeUpdate(CREATE_TABLE);
      connection.createStatement().executeUpdate(CREATE_IMG_TABLE);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    long previousKey = 0;
    levels.put((long) 0, 0);
    for (int level = 0; level <= MAX_LEVEL; ++level) {
      int key = 5 * (level * level + 8 * level + 11);
      previousKey += key;
      levels.put(previousKey, level);
    }
  }

  @Override
  public void onStatusChange(StatusChangeEvent event) {
    if (event.getNewStatus() == JDA.Status.CONNECTED && !registered) {
      new Timer().scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
          try {
            Map<Integer, LevelingToken> result = getTop(5000);
            topCache = result;
            int curPos = 1;
            EmbedBuilder builder = new EmbedBuilder();
            builder.setAuthor("Top 25 chatters by XP", null, C.getGuild().getIconUrl())
                .setTimestamp(OffsetDateTime.now())
                .setColor(Color.MAGENTA);

            StringBuilder description = new StringBuilder();
            for (Map.Entry<Integer, LevelingToken> mapToken : result.entrySet()) {
              if (curPos - 1 == 25) {
                break;
              }

              if (curPos > 1) {
                description.append("\n"
                        + "\n");
              }

              LevelingToken token = mapToken.getValue();

              long rankXp = getNextExp(token.getLevel()).longValue();
              long totalExpP = token.getLevel() - 1;
              long progressXp;
              if (token.getLevel() == 0) {
                progressXp = 0;
              } else {
                progressXp = token.getExp() - getTotalExp(totalExpP) - getNextExp(totalExpP).longValue();
              }

              builder.addField(curPos + " **" + C.getFullName(token.getMember().getUser()) + "**", "Level " + token.getLevel() + ", XP Progress " + C.prettyNum(progressXp) + "/" + C.prettyNum(rankXp) + ", and Total XP " + C.prettyNum(token.getExp()), true);
//              description.append(C.getPositionName(curPos))
//                  .append(" ")
//                  .append("**").append(C.getFullName(token.getMember().getUser())).append("**")
//                  .append("'s Level is ")
//                  .append(token.getLevel())
//                  .append(", XP Progress ")
//                  .append(C.prettyNum(progressXp))
//                  .append("/")
//                  .append(C.prettyNum(rankXp))
//                  .append(", and Total XP: ")
//                  .append(C.prettyNum(token.getExp()))
//                  .append(" ");
              curPos++;
            }
            builder.setDescription(description);
            Channels.LEADERBOARD.getChannel()
                .retrieveMessageById(leaderboardMessageId).complete().editMessage(builder.build()).queue();
          } catch (SQLException e) {
            log.error("Error While Processing Leaderboard");
            e.printStackTrace();
          }
        }
      }, 0, 300000);
      registered = true;
    }
  }

  public boolean isPastUser(String userId) {
    try {
      PreparedStatement statement = connection.prepareStatement(SELECT_USER);
      statement.setString(1, userId);
      return statement.executeQuery().next();
    } catch (SQLException e) {
      return false;
    }
  }

  public void spawnUser(String userId) {
    try {
      PreparedStatement statement = connection.prepareStatement(CREATE_USER);
      statement.setString(1, userId);
      statement.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public long getExp(String userId) {
    try {
      PreparedStatement statement = connection.prepareStatement(SELECT_USER);
      statement.setString(1, userId);
      ResultSet rs = statement.executeQuery();
      rs.next();
      return rs.getLong("level");
    } catch (SQLException e) {
      return -1;
    }
  }

  public BigInteger getNextExp(long lvl) {
    return new Expression("5 * (" + lvl + " ^ 2) + 50 * " + lvl + " + 100").eval().toBigInteger();
  }

  public void addExp(String userId, int amount) {
    try {
      PreparedStatement statement = connection.prepareStatement(UPDATE_USER);
      statement.setLong(1, getExp(userId) + amount);
      statement.setString(2, userId);
      statement.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public boolean hasImage(String userid) {
    try {
      PreparedStatement statement = connection.prepareStatement(SELECT_IMAGE);
      statement.setString(1, userid);
      return statement.executeQuery().next();
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public void setImage(String userid, BufferedImage image) {
    try {
      if (hasImage(userid)) {
        PreparedStatement statement = connection.prepareStatement(UPDATE_IMAGE);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);
        statement.setBlob(1, new ByteArrayInputStream(os.toByteArray()));
        statement.setString(2, userid);
        statement.execute();
      } else {
        PreparedStatement statement = connection.prepareStatement(INSERT_IMAGE);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);
        statement.setBlob(2, new ByteArrayInputStream(os.toByteArray()));
        statement.setString(1, userid);
        statement.execute();
      }
    } catch (SQLException | IOException e) {
      e.printStackTrace();
    }
  }

  public BufferedImage getImage(String userId) {
    try {
      PreparedStatement statement = connection.prepareStatement(SELECT_IMAGE);
      statement.setString(1, userId);
      ResultSet set = statement.executeQuery();
      set.next();
      return ImageIO.read(set.getBlob("img").getBinaryStream());
    } catch (SQLException | IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  public Integer toLevel(long expAmount) {
    return levels.floorEntry(expAmount).getValue();
  }

  public long getTotalExp(long level) {
    Object[] keys = levels.entrySet().stream().filter(e -> level == e.getValue())
        .map(Map.Entry::getKey).toArray();
    return (long) keys[0];
  }

  public Map<Integer, LevelingToken> getTop(int amount) throws SQLException {
    Map<Integer, LevelingToken> topBal = new HashMap<>();
    ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM levels ORDER BY level DESC LIMIT 0," + amount + ";");
    for (int i = 0; i < amount; i++) {
      do {
        if (!resultSet.next()) {
          return topBal;
        }
      } while (C.getGuild().getMemberById(resultSet.getString("userId")) == null);
      topBal.put(i + 1, new LevelingToken(C.getGuild().getMemberById(resultSet.getString("userId")), resultSet.getLong("level"), toLevel(resultSet.getLong("level"))));
    }
    return topBal;
  }

  @Override
  public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

    //Make sure the message is from an actual user
    if (event.isWebhookMessage() || event.getAuthor().isBot()) {
      return;
    }

    if (C.hasRole(event.getMember(), Roles.EXP_SPAMMER)) {
      return;
    }

    String userId = event.getAuthor().getId();
    if (!isPastUser(userId)) {
      spawnUser(userId);
    }

    if (lastChatTimes.containsKey(userId) && getTimeRemaining(userId) > 0) {
      return;
    }
    lastChatTimes.put(userId, OffsetDateTime.now().plusMinutes(1));

    //Make sure we are not awarding xp to commands
    String content = event.getMessage().getContentRaw();
    if (content.startsWith("!") || content.startsWith("^") || content.startsWith("?") || content.startsWith("a.")) {
      if (event.getChannel().getId().equals(Channels.CASINO.getId())) {
        if (!content.startsWith("^gamble") || !content.startsWith("^rob")) {
          return;
        }
      } else {
        return;
      }
    }

    int expA = random.nextInt(25 - 15) + 15;
    addExp(userId, expA);

    if (toLevel(getExp(userId)) > levelCache.getUnchecked(userId)) {
      processRewards(toLevel(getExp(userId)), Objects.requireNonNull(event.getMember()), event.getChannel());
    }

    levelCache.put(userId, toLevel(getExp(userId)));
  }

  @Override
  public void onGuildMemberJoin(GuildMemberJoinEvent e) {
    if (isPastUser(e.getUser().getId())) {
      int level = toLevel(getExp(e.getUser().getId()));

      if (level >= 65) {
        C.giveRoles(e.getMember(), Roles.LEGENDARY, Roles.OG, Roles.OBSESSIVE, Roles.TRYHARD, Roles.REGULAR, Roles.FANS);
      } else if (level >= 50) {
        C.giveRoles(e.getMember(), Roles.OG, Roles.OBSESSIVE, Roles.TRYHARD, Roles.REGULAR, Roles.FANS);
      } else if (level >= 30) {
        C.giveRoles(e.getMember(), Roles.OBSESSIVE, Roles.TRYHARD, Roles.REGULAR, Roles.FANS);
      } else if (level >= 20) {
        C.giveRoles(e.getMember(), Roles.TRYHARD, Roles.REGULAR, Roles.FANS);
      } else if (level >= 10) {
        C.giveRoles(e.getMember(), Roles.REGULAR, Roles.FANS);
      } else {
        C.giveRole(e.getMember(), Roles.FANS);
      }
    } else {
      C.giveRole(e.getMember(), Roles.FANS);
    }
  }

  private void processRewards(int level, Member member, TextChannel channel) {
    StringBuilder sb = new StringBuilder();
    String randomMessage = messageFactory.getRawMessage(MessageFactory.MessageType.LEVEL).replaceAll("<user>", member.getAsMention()).replaceAll("<level>", C.bold("Level " + level));

    sb.append(randomMessage);

    switch (level) {
      case 10: {
        sb.append("\n    + Regular Rank");
        C.giveRole(member, Roles.REGULAR);
        break;
      }
      case 20: {
        sb.append("\n    + Tryhard Rank");
        C.giveRole(member, Roles.TRYHARD);
        break;
      }
      case 30: {
        sb.append("\n    + Obsessive Rank");
        C.giveRole(member, Roles.OBSESSIVE);
        break;
      }
      case 50: {
        sb.append("\n    + OG Rank");
        C.giveRole(member, Roles.OG);
        break;
      }
      case 65: {
        sb.append("\n    + Legendary Rank");
        sb.append("\n    + Gamble x1 Multiplier");
        sb.append("\n    + Custom ^rank Background");
        C.giveRole(member, Roles.LEGENDARY);
        purchaseManager.addReward(member.getUser().getId(), Reward.DAILY1);
        break;
      }
      default: { //No Special Reward: Regular Level-Up Message.
        break;
      }
    }

    channel.sendMessage(sb.toString()).queue();
  }

  private int getTimeRemaining(String userId) {
    return (int) OffsetDateTime.now().until(lastChatTimes.get(userId), ChronoUnit.SECONDS);
  }

}
