package io.github.jroy.happybot.events.star;

import io.github.jroy.happybot.sql.SQLManager;
import io.github.jroy.happybot.util.*;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("FieldCanBeLocal")
public class StarMessages extends ListenerAdapter {

    private Connection connection;

    private final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS starstats ( `id` INT(50) NOT NULL AUTO_INCREMENT , `userid` VARCHAR(50) NOT NULL , `stars` BIGINT(255) NOT NULL DEFAULT '0' , `gilds` BIGINT(255) NOT NULL DEFAULT '0' , `heels` BIGINT(255) NULL DEFAULT '0' , PRIMARY KEY (`id`)) ENGINE = InnoDB;";
    private final String SELECT_USER = "SELECT * FROM starstats WHERE userid = ?;";
    private final String CREATE_USER = "INSERT INTO starstats (userId) VALUES (?);";
    private final String UPDATE_USER = "UPDATE starstats SET stars = ?, gilds = ?, heels = ? WHERE userId = ?;";

    private static final String STAR = "⭐";
    private static final String SHOE = "\uD83D\uDC60";

    private HashSet<String> alreadyUsedMessages = new HashSet<>();

    public StarMessages(SQLManager sqlManager) {
        connection = sqlManager.getConnection();
        try {
            connection.createStatement().executeUpdate(CREATE_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public StarStatsToken getUser(String userId) {
        try {
            if (!isPropagated(userId))
                createUser(userId);
            PreparedStatement statement = connection.prepareStatement(SELECT_USER);
            statement.setString(1, userId);
            return new StarStatsToken(statement.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isPropagated(String userId) {
        try {
            PreparedStatement statement = connection.prepareStatement(SELECT_USER);
            statement.setString(1, userId);
            return statement.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void createUser(String userId) {
        try {
            PreparedStatement statement = connection.prepareStatement(CREATE_USER);
            statement.setString(1, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    private void addGild(String userId) {
        try {
            StarStatsToken token = getUser(userId);
            PreparedStatement statement = connection.prepareStatement(UPDATE_USER);
            statement.setLong(1, token.getStarCount());
            statement.setLong(2, token.getGildCount() + 1);
            statement.setLong(3, token.getHeelCount());
            statement.setString(4, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
        if (e.getReactionEmote().getName().equals(STAR) || e.getReactionEmote().getName().equals(SHOE)) {
            handleStar(e, e.getReactionEmote().getName().equals(SHOE));
        } else if (e.getReactionEmote().getName().equals("gild")) {
            if (!C.hasRole(e.getMember(), Roles.MODERATOR)) {
                e.getReaction().removeReaction().queue();
                return;
            }
            e.getChannel().getMessageById(e.getMessageId()).queue(message -> {
                if (!RuntimeEditor.isAllowSelfGilds()) {
                    if (e.getMember().getUser() == message.getAuthor()) {
                        e.getReaction().removeReaction().queue();
                        return;
                    }
                }
                handleGild(e);
            });
        }
    }

    private void handleStar(GuildMessageReactionAddEvent e, boolean shoe) {
      CompletableFuture.runAsync(new HandleStar(e, shoe));
    }

    private void handleGild(GuildMessageReactionAddEvent e) {
        CompletableFuture.runAsync(new HandleGild(e));
    }

    private void sendStarredMessage(String footer, Message message, String privateMessageText, boolean shoe) {
        EmbedBuilder embed = new EmbedBuilder()
            .setTitle(C.getFullName(message.getAuthor()))
            .setDescription(message.getContentRaw());

        if (message.getEmbeds().size() > 0) {
            message.getChannel().sendMessage("Failed to Star/Gild/Shoe a Message: Contained an un-readable embed! Cannot Continue!").queue();
            return;
        }

        boolean gild = footer.startsWith("New Gilded Message");
        if (shoe)
            embed.setFooter(footer, "https://i.imgur.com/Gq5xooX.png");
        else if (gild)
            embed.setFooter(footer, "https://cdn.discordapp.com/emojis/371121885997694976.png?v=1");
        else
            embed.setFooter(footer, "https://i.imgur.com/WwPHcgU.png"); //Star

        embed.setThumbnail(message.getMember().getUser().getAvatarUrl())
        .setColor(message.getMember().getColor());
        if (C.containsImage(message)) {
            embed.setImage(C.getImage(message));
        }
        Channels.STARRED_MESSAGES.getChannel().sendMessage(embed.build()).queue();
        C.privChannel(message.getMember(), privateMessageText);
        C.privChannel(message.getMember(), embed.build());
        alreadyUsedMessages.add(message.getId());
    }

    private class HandleStar implements Runnable {

        private GuildMessageReactionAddEvent e;
        private Message message;
        private boolean shoe;
        private static final int NUM_STARS_REQUIRED = 5;

        HandleStar(GuildMessageReactionAddEvent e, boolean shoe) {
            this.e = e;
            this.shoe = shoe;
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
                //noinspection ConstantConditions
                int numberOfStars = message.getReactions().stream()
                        .filter(reaction -> reaction.getReactionEmote().getName().equals(STAR) || reaction.getReactionEmote().getName().equals(SHOE))
                        .findAny().map(MessageReaction::getCount).orElse(0);

                if (numberOfStars >= NUM_STARS_REQUIRED && !alreadyUsedMessages.contains(message.getId())) {
                    String footer = "New " + ((shoe) ? "Heeled" : "Starred") + " Message from #" + message.getChannel().getName();
                    String privateMessageText = "Congrats! One of your messages has been " + ((shoe) ? "heeled" : "starred") +":";
                    sendStarredMessage(footer, message, privateMessageText, shoe);
                    if (shoe) {
                        addHeel(message.getAuthor().getId());
                    } else {
                        addStar(message.getAuthor().getId());
                    }
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
                String footer = "New Gilded Message from #" + message.getChannel().getName() + " (" + C.getFullName(e.getUser()) + ")";
                String privateMessageText = "Congrats! One of your messages has been gilded by a staff member:";
                alreadyUsedMessages.add(message.getId());
                sendStarredMessage(footer, message, privateMessageText, false);
                addGild(message.getAuthor().getId());
            }
        }
    }
}
