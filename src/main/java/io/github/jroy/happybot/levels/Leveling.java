package io.github.jroy.happybot.levels;

import com.udojava.evalex.Expression;
import io.github.jroy.happybot.Main;
import io.github.jroy.happybot.sql.MessageFactory;
import io.github.jroy.happybot.sql.SQLManager;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Channels;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.StatusChangeEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@SuppressWarnings("FieldCanBeLocal")
public class Leveling extends ListenerAdapter {

    private final Connection connection;
    private final MessageFactory messageFactory;

    private final static int MAX_LEVEL = 200;

    private final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `levels` ( `id` INT NOT NULL AUTO_INCREMENT , `userId` VARCHAR(255) NOT NULL , `level` BIGINT(255) NOT NULL DEFAULT '0' , UNIQUE (`id`)) ENGINE = InnoDB;";
    private final String SELECT_USER = "SELECT * FROM `levels` WHERE userId = ?;";
    private final String CREATE_USER = "INSERT INTO `levels` (userId) VALUES (?);";
    private final String UPDATE_USER = "UPDATE `levels` SET level = ? WHERE userId = ?;";

    private boolean registered = false;
    private final String leaderboardMessageId = "464507047657078804";

    private final Random random = new Random();
    private TreeMap<Long, Integer> levels = new TreeMap<>();

    private HashMap<String, OffsetDateTime> lastChatTimes = new HashMap<>();
    private HashMap<String, Integer> levelCache = new HashMap<>();
    public Map<Integer, LevelingToken> topCache = new HashMap<>();

    public Leveling(SQLManager sqlManager, MessageFactory messageFactory) {
        this.connection = sqlManager.getConnection();
        this.messageFactory = messageFactory;
        try {
            connection.createStatement().executeUpdate(CREATE_TABLE);
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
                        Map<Integer, LevelingToken> result = getTop(25);
                        topCache = result;
                        int curPos = 1;
                        EmbedBuilder builder = new EmbedBuilder();
                        builder.setAuthor("Top 25 XP Betrayals", null, C.getGuild().getIconUrl()).setFooter("Stats provided by happybot's Leveling API!", Main.getJda().getSelfUser().getAvatarUrl()).setColor(Color.MAGENTA).setDescription("Here are the current top 25 rankings for experience.");
                        for (Map.Entry<Integer, LevelingToken> mapToken : result.entrySet()) {
                            LevelingToken token = mapToken.getValue();
                            builder.addField("- #" + curPos + ": " + C.getFullName(token.getMember().getUser()), "Level " + token.getLevel() + " (" + C.prettyNum((int) token.getExp()) + " XP)", false);
                            curPos++;
                        }
                        Channels.LEADERBOARD.getChannel().getMessageById(leaderboardMessageId).complete().editMessage(builder.build()).queue();
                    } catch (SQLException e) {
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

    public BigInteger getNextExp(int lvl) {
        return new Expression("5 * ("+lvl+" ^ 2) + 50 * "+lvl+" + 100").eval().toBigInteger();
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

    public Integer toLevel(long expAmount) {
        return levels.floorEntry(expAmount).getValue();
    }

    public long getTotalExp(Integer level) {
        Object[] keys = levels.entrySet().stream().filter(e -> level.equals(e.getValue()))
                .map(Map.Entry::getKey).toArray();
        return (long) keys[0];
    }

    public Map<Integer, LevelingToken> getTop(int amount) throws SQLException {
        Map<Integer, LevelingToken> topBal = new HashMap<>();
        ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM levels ORDER BY level DESC LIMIT 0,"+ amount +";");
        int max = 0;
        for (int i = 0; i < amount; i++) {
            do {
                if (!resultSet.next()) {
                    return topBal;
                }
            } while (C.getGuild().getMemberById(resultSet.getString("userId")) == null);
            topBal.put(i + 1, new LevelingToken(C.getGuild().getMemberById(resultSet.getString("userId")), resultSet.getLong("level"), toLevel(resultSet.getLong("level"))));
            max++;
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

        //Make sure we are not awarding xp to commands
        String content = event.getMessage().getContentRaw();
        if (content.startsWith("!") || content.startsWith("^") || content.startsWith("?") || content.startsWith("a.")) {
            return;
        }

        String userId = event.getAuthor().getId();
        if (!isPastUser(userId)) {
            spawnUser(userId);
        }
        if (lastChatTimes.containsKey(userId) && getTimeRemaining(userId) > 0) {
            return;
        }

        if (!levelCache.containsKey(userId)) {
            levelCache.put(userId, toLevel(getExp(userId)));
        }

        int expA = random.nextInt(25-15) + 15;
        addExp(userId, expA);
        lastChatTimes.put(userId, OffsetDateTime.now().plusMinutes(1));

        if (toLevel(getExp(userId)) > levelCache.get(userId)) {
            processRewards(toLevel(getExp(userId)), event.getMember(), event.getChannel());
        }

        levelCache.put(userId, toLevel(getExp(userId)));
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        if (isPastUser(e.getUser().getId())) {
            int level = toLevel(getExp(e.getUser().getId()));

            if (level >= 50) {
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
        String randomMessage = messageFactory.getRawMessage(MessageFactory.MessageType.LEVEL).replaceAll("<user>", member.getAsMention()).replaceAll("<level>", C.bold("Level " + String.valueOf(level)));

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