package io.github.jroy.happybot.game;

import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.jroy.happybot.Main;
import io.github.jroy.happybot.game.elo.EloKey;
import io.github.jroy.happybot.game.elo.EloManager;
import io.github.jroy.happybot.game.elo.EloPlayer;
import io.github.jroy.happybot.game.elo.EloResult;
import io.github.jroy.happybot.game.model.GameMessageReceived;
import io.github.jroy.happybot.game.model.GameReactionReceived;
import io.github.jroy.happybot.game.model.GameStartEvent;
import io.github.jroy.happybot.game.model.PendingGameToken;
import io.github.jroy.happybot.sql.SQLManager;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Categories;
import io.github.jroy.happybot.util.Channels;
import io.github.jroy.happybot.util.Roles;
import java.text.DecimalFormat;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.StatusChangeEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
public class GameManager extends ListenerAdapter {
  private static final DecimalFormat CHANGE_FORMAT = new DecimalFormat("(+#);(-#)");

  private final SQLManager sqlManager;
  @Getter
  private final EloManager eloManager;
  /**
   * String: Message ID that also serves as a prompt
   * PendingGameToken: The information about the pending game.
   */
  private final Cache<String, PendingGameToken> pendingStart = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();
  /**
   * String: Member ID that is pending.
   * String: Message ID of the prompt.
   */
  private final Cache<String, String> pendingUsers = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();
  /**
   * Integer: Game ID for spectating.
   * ActiveGame: The game being played.
   */
  private final Map<Integer, ActiveGame> activeGames = new HashMap<>();
  /**
   * Maps Private Channel IDs to the Game IDs.
   * <p>
   * String: Private Channel ID
   * Integer: Game ID
   */
  private final Map<String, Integer> channelToIdMap = new HashMap<>();
  /**
   * List of users active in game; Whether they created it of are playing in one.
   * <p>
   * String: User ID
   */
  private final Set<String> activeUsers = new HashSet<>();
  private int gameId = 1;
  private boolean statusLoaded = false;
  private boolean pendingRestart = false;
  private int pendingCode = 0;

  public GameManager(SQLManager sqlManager, EloManager eloManager) {
    this.sqlManager = sqlManager;
    this.eloManager = eloManager;
    new Timer().scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        for (Map.Entry<Integer, ActiveGame> curEntry : new HashMap<>(activeGames).entrySet()) {
          if (OffsetDateTime.now().until(curEntry.getValue().getLastAction(), ChronoUnit.MINUTES) <= 0) {
            C.privChannel(curEntry.getValue().getCreator(), "Your game has timed out and has been closed!");
            activeUsers.remove(curEntry.getValue().getCreator().getUser().getId());
            channelToIdMap.remove(curEntry.getValue().getChannel().getId());
            curEntry.getValue().getChannel().delete().queue();
            activeGames.remove(curEntry.getValue().getId());
          }
        }
        checkRestart(0);
      }
    }, 0, 120000);
  }

  private void checkRestart(int gameCount) {
    if (activeGames.size() == gameCount && pendingRestart) {
      Main.getJda().shutdown();
      log.warn("The JDA instance has been shutdown...exiting the program.");
      System.exit(pendingCode);
    }
  }

  @Override
  public void onStatusChange(StatusChangeEvent event) {
    if (event.getNewStatus() == JDA.Status.CONNECTED && !statusLoaded) {
      Categories.GAMES.getCategory().getChannels().forEach(channel -> channel.delete().queue());
      statusLoaded = true;
    }
  }

  @Override
  public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent e) {
    if (isPendingRestart()) {
      return;
    }
    if (C.hasRole(e.getMember(), Roles.MUTED)) {
      return;
    }
    if (!e.getUser().isBot() && pendingStart.asMap().containsKey(e.getMessageId()) && e.getReactionEmote().getName().equalsIgnoreCase("\uD83D\uDC4D")) {
      PendingGameToken token = pendingStart.getIfPresent(e.getMessageId());
      String userId = e.getMember().getUser().getId();
      assert token != null;
      if (!isActive(userId) && !token.getPlayers().contains(e.getMember())) {
        token.addPlayer(e.getMember());
        if (token.getPlayers().size() == token.getGame().getMaxPlayers()) {
          startGame(token.getMember());
          return;
        }
        if (token.getPlayers().size() >= token.getGame().getMinPlayers()) {
          token.getMessage().getTextChannel().sendMessage("Hello " + token.getMember().getAsMention() + ", your game now has enough people to start it! Please do `^game start` to start your game.").queue();
        }
      }
    }
    if (channelToIdMap.containsKey(e.getChannel().getId())) {
      activeGames.get(channelToIdMap.get(e.getChannel().getId())).getGame().reactionReceived(new GameReactionReceived(e, activeGames.get(channelToIdMap.get(e.getChannel().getId()))));
    }
  }

  @Override
  public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
    if (channelToIdMap.containsKey(e.getChannel().getId()) && !e.isWebhookMessage() && !e.getAuthor().isBot()) {
      int id = channelToIdMap.get(e.getChannel().getId());
      ActiveGame activeGame = activeGames.get(id);
      activeGame.setLastAction(OffsetDateTime.now().plus(10, ChronoUnit.MINUTES));
      Game game = activeGame.getGame();

      // debug command to auto win
      if (e.getMessage().getContentRaw().equalsIgnoreCase("win") && C.hasRole(e.getMember(), Roles.SUPER_ADMIN)) {
        game.endGame(activeGame, e.getMember());
      } else {
        game.messageReceived(new GameMessageReceived(activeGame, e));
      }
    }
  }

  public boolean isPendingRestart() {
    return pendingRestart;
  }

  public void setPendingRestart(boolean pendingRestart) {
    this.pendingRestart = pendingRestart;
  }

  public void setPendingCode(int pendingCode) {
    this.pendingCode = pendingCode;
  }

  public boolean isGamesActive() {
    return activeGames.size() > 0;
  }

  public boolean isPendingUser(String userId) {
    return pendingUsers.asMap().containsKey(userId);
  }

  public PendingGameToken getPendingToken(Member member) {
    return getPendingToken(pendingUsers.asMap().get(member.getUser().getId()));
  }

  public PendingGameToken getPendingToken(String messageId) {
    return pendingStart.asMap().get(messageId);
  }

  public boolean isActive(String userId) {
    boolean match = false;
    outLoop:
    for (Map.Entry<String, PendingGameToken> pendingGame : pendingStart.asMap().entrySet()) {
      for (Member curMem : pendingGame.getValue().getPlayers()) {
        if (curMem.getUser().getId().equals(userId)) {
          match = true;
          break outLoop;
        }
      }
    }
    return activeUsers.contains(userId) || match;
  }

  /**
   * Determines if a user has a game in progress.
   *
   * @param userId The user id of the creator of the game
   * @return Returns true if the the user id provided has a game in progress.
   */
  public boolean isHosting(String userId) {
    for (Map.Entry<Integer, ActiveGame> curEntry : activeGames.entrySet()) {
      if (curEntry.getValue().getCreator().getUser().getId().equals(userId)) {
        return true;
      }
    }
    return false;
  }

  public void pendGame(Message message, Member member, Game game) {
    Message prompt = message.getTextChannel().sendMessage(new EmbedBuilder().setTitle("New Game Started!").setDescription(C.getFullName(member.getUser()) + " has started a game. Please react with :+1: to join the game!\n\n**Game:** " + game.getName() + "\n**Description:** " + game.getDescription() + "\n**Minimum Players:** " + game.getMinPlayers() + "\n**Maximum Players:** " + game.getMaxPlayers() + "\n\n**This invite will expire in 5 minutes!**").build()).complete();
    prompt.addReaction("\uD83D\uDC4D").complete();
    PendingGameToken token = new PendingGameToken(prompt.getId(), message, member, game);
    pendingStart.put(prompt.getId(), token);
    pendingUsers.put(member.getUser().getId(), prompt.getId());
    if (token.getGame().getMaxPlayers() == 1) {
      startGame(member);
    }
  }

  public void startGame(Member member) {
    String prompt = pendingUsers.getIfPresent(member.getUser().getId());
    assert prompt != null;
    PendingGameToken token = pendingStart.getIfPresent(prompt);
    pendingStart.invalidate(prompt);
    pendingUsers.invalidate(member.getUser().getId());
    assert token != null;
    spawnGame(token.getMessage(), token.getMember(), token.getGame(), token.getPlayers());
  }

  /**
   * Creates a game along with all the things that go with it.
   *
   * @param message The message that the game was requested from.
   * @param member  The creator of the game.
   * @param game    The requested game.
   */
  public void spawnGame(Message message, Member member, Game game, Set<Member> players) {
    int gameId = this.gameId++;
    TextChannel newChannel = Categories.GAMES.getCategory().createTextChannel("game-" + gameId).setTopic("Playing " + game.getName() + "! Started by " + C.getFullName(member.getUser()))
        .addPermissionOverride(C.getGuild().getPublicRole(), null, EnumSet.of(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ))
        .addPermissionOverride(member, EnumSet.of(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ), null)
        .complete();
    for (Member curPlayer : players) {
      activeUsers.add(curPlayer.getUser().getId());
      if (newChannel.getPermissionOverride(curPlayer) == null) {
        newChannel.createPermissionOverride(curPlayer).setAllow(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ).queue();
      } else {
        Objects.requireNonNull(newChannel.getPermissionOverride(curPlayer)).getManager().grant(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ).queue();
      }
    }
    ActiveGame activeGame = new ActiveGame(gameId, newChannel, Objects.requireNonNull(C.getGuild().getTextChannelById(newChannel.getId())).createWebhook("game communication").complete(), game, member, players);
    activeGames.put(gameId, activeGame);
    channelToIdMap.put(newChannel.getId(), gameId);
    activeUsers.add(member.getUser().getId());


    GameType type = GameType.getGameType(game);
    if (type.isSuportsElo()) {
      Iterator<Member> it = players.iterator();
      Member first = it.next();
      EloPlayer firstPlayer = eloManager.getPlayer(new EloKey(first.getId(), type));
      Member second = it.next();
      EloPlayer secondPlayer = eloManager.getPlayer(new EloKey(second.getId(), type));
      if (firstPlayer != null && secondPlayer != null) {
        activeGame.sendMessage(new WebhookEmbedBuilder().setDescription(
            first.getAsMention() + ", you have a rating of " + (int) firstPlayer.getEloRating() + ".\n"
                + second.getAsMention() + ", you have a rating of " + (int) secondPlayer.getEloRating() + ".")
            .build());
      }
    }

    activeGame.getGame().gameStart(new GameStartEvent(activeGame));
  }

  /**
   * Locks a game channel and awards winner prizes.
   *
   * @param activeGame The active game instance.
   * @param winner     The winner of the game. Can be null to indicate no winner.
   * @param coinPrize  The amount of coins to award the user.
   */
  public void protectGame(ActiveGame activeGame, @Nullable Member winner, int coinPrize) {
    for (Member curPlayer : activeGame.getPlayers()) {
      Objects.requireNonNull(activeGame.getChannel().getPermissionOverride(curPlayer)).getManager().deny(Permission.MESSAGE_WRITE).queue();
    }
    activeGame.sendMessage("---------------------------------------------------------------");
    GameType gameType = GameType.getGameType(activeGame.getGame());
    if (winner != null) {
      Member loser = null;
      if (gameType.isSuportsElo()) {
        for (Member player : activeGame.getPlayers()) {
          if (!winner.equals(player)) {
            loser = player;
            break;
          }
        }
      }

      EloResult result = null;
      if (loser != null) {
        result = eloManager.calculate(gameType, winner.getUser(), loser.getUser(), false);
      }

      String message = "Congratulations " + winner.getAsMention() + " for winning the game!\n" +
          "You have been awarded **" + C.prettyNum(coinPrize) + " coins**!\n" +
          "Spend them in `^shop`!";

      if (result != null) {
        message += "\n\nWinner rating: " + winner.getAsMention() + ": " + result.getWinnerElo() + " " + CHANGE_FORMAT.format(result.getWinnerEloChange())
            + "\nLoser rating: " + loser.getAsMention() + ": " + result.getLoserElo() + " " + CHANGE_FORMAT.format(result.getLoserEloChange());
      }

      activeGame.sendMessage(message);
      try {
        if (!sqlManager.isActiveUserH(winner.getUser().getId())) {
          sqlManager.newUser(winner.getUser().getId());
        }
        sqlManager.getUser(winner.getUser().getId()).addCoins(coinPrize);
      } catch (SQLException e) {
        activeGame.sendMessage("Unable to award prize. Please notify a developer!");
        Channels.STAFF_QUEUE.getChannel().sendMessage(
            "Please award " + winner.getAsMention() + " with **" + C.prettyNum(coinPrize) + " coins** for a game reward. I attempted to do this on my one but failed. Also, let a developer know?")
            .queue();
      }
    } else {
      Member p0 = null;
      Member p1 = null;
      if (gameType.isSuportsElo()) {
        for (Member player : activeGame.getPlayers()) {
          if (p0 == null) {
            p0 = player;
          } else {
            p1 = player;
            break;
          }
        }
      }

      EloResult result = null;
      if (p0 != null && p1 != null) {
        result = eloManager.calculate(gameType, p0.getUser(), p1.getUser(), true);
      }

      String message = "Nobody Won the Game :(\n" +
          "Better Luck Next Time!\n" +
          "This channel will be removed in approximately **1 minute!**\n" +
          "---------------------------------------------------------------";
      if (result != null) {
        message += "\n\nWinner rating: " + p0.getAsMention() + ": " + result.getWinnerElo() + " " + CHANGE_FORMAT.format(result.getWinnerEloChange())
            + "\nLoser rating: " + p1.getAsMention() + ": " + result.getLoserElo() + " " + CHANGE_FORMAT.format(result.getLoserEloChange());
      }
      activeGame.sendMessage(message);
    }
    checkRestart(1);
    new Timer().schedule(new TimerTask() {
      @Override
      public void run() {
        if (activeGames.containsKey(activeGame.getId())) {
          stopGame(activeGame.getCreator());
        }
      }
    }, 60000);
  }

  /**
   * Stops a game and cleans up the channels.
   *
   * @param member The creator of the game.
   */
  public void stopGame(Member member) {
    ActiveGame activeGame = null;
    for (Map.Entry<Integer, ActiveGame> curEntry : activeGames.entrySet()) {
      if (curEntry.getValue().getCreator().getUser().getId().equals(member.getUser().getId())) {
        activeGame = curEntry.getValue();
      }
    }
    if (activeGame == null) {
      return;
    }
    activeUsers.remove(activeGame.getCreator().getUser().getId());
    for (Member curPlayer : activeGame.getPlayers()) {
      activeUsers.remove(curPlayer.getUser().getId());
    }
    channelToIdMap.remove(activeGame.getChannel().getId());
    activeGame.getChannel().delete().queue();
    activeGames.remove(activeGame.getId());
    checkRestart(0);
  }
}
