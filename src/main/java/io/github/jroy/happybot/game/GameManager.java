package io.github.jroy.happybot.game;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.jroy.happybot.game.model.GameMessageReceived;
import io.github.jroy.happybot.game.model.GameReactionReceived;
import io.github.jroy.happybot.game.model.GameStartEvent;
import io.github.jroy.happybot.game.model.PendingGameToken;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Categories;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.StatusChangeEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class GameManager extends ListenerAdapter {
  private int gameId = 1;
  private boolean statusLoaded = false;

  /**
   * String: Message ID that also serves as a prompt
   * PendingGameToken: The information about the pending game.
   */
  private Cache<String, PendingGameToken> pendingStart = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();
  /**
   * String: Member ID that is pending.
   * String: Message ID of the prompt.
   */
  private Cache<String, String> pendingUsers = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();
  /**
   * Integer: Game ID for spectating.
   * ActiveGame: The game being played.
   */
  private Map<Integer, ActiveGame> activeGames = new HashMap<>();
  /**
   * Maps Private Channel IDs to the Game IDs.
   * <p>
   * String: Private Channel ID
   * Integer: Game ID
   */
  private Map<String, Integer> channelToIdMap = new HashMap<>();
  /**
   * List of users active in game; Whether they created it of are playing in one.
   * <p>
   * String: User ID
   */
  private Set<String> activeUsers = new HashSet<>();

  public GameManager() {
    new Timer().scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        for (Map.Entry<Integer, ActiveGame> curEntry : activeGames.entrySet()) {
          if (OffsetDateTime.now().until(curEntry.getValue().getLastAction(), ChronoUnit.MINUTES) <= 0) {
            C.privChannel(curEntry.getValue().getCreator(), "Your game has timed out and has been closed!");
            activeUsers.remove(curEntry.getValue().getCreator().getUser().getId());
            channelToIdMap.remove(curEntry.getValue().getChannel().getId());
            curEntry.getValue().getChannel().delete().queue();
            activeGames.remove(curEntry.getValue().getId());
          }
        }
      }
    }, 0, 120000);
  }

  @Override
  public void onStatusChange(StatusChangeEvent event) {
    if (event.getNewStatus() == JDA.Status.CONNECTED && !statusLoaded) {
      Categories.GAMES.getCategory().getChannels().forEach(channel -> channel.delete().queue());
      statusLoaded = true;
    }
  }

  @Override
  public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
    if (pendingStart.asMap().containsKey(e.getMessageId())  && e.getReactionEmote().getEmote().getName().equalsIgnoreCase("\uD83D\uDC4D")) {
      PendingGameToken token = pendingStart.getIfPresent(e.getMessageId());
      String userId = e.getMember().getUser().getId();
      assert token != null;
      if (!activeUsers.contains(userId) && !token.getPlayers().contains(e.getMember())) {
        token.addPlayer(e.getMember());
        activeUsers.add(e.getMember().getUser().getId());
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
      activeGames.get(id).setLastAction(OffsetDateTime.now().plus(10, ChronoUnit.MINUTES));
      activeGames.get(id).getGame().messageReceived(new GameMessageReceived(activeGames.get(id), e));
    }
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
    return activeUsers.contains(userId);
  }

  /**
   * Determains if a user has a game in progress.
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
    Message prompt = message.getTextChannel().sendMessage(new EmbedBuilder().setTitle("New Game Started!").setDescription(C.getFullName(member.getUser()) + " has started a game. Please react with :+1: to join the game!\n**Game:** " + game.getName() + "\n**Description:** " + game.getDescription() + "\n**Minimum Players:** " + game.getMinPlayers() + "\n**Maximum Players:** " + game.getMaxPlayers() + "\n\n**This invite will expire in 5 minutes!**").build()).complete();
    prompt.addReaction("\uD83D\uDC4D").complete();
    PendingGameToken token = new PendingGameToken(prompt.getId(), message, member, game);
    pendingStart.put(prompt.getId(), token);
    pendingUsers.put(member.getUser().getId(), prompt.getId());
    activeUsers.add(member.getUser().getId());
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
    Channel newChannel = Categories.GAMES.getCategory().createTextChannel("game-" + gameId).setTopic("Playing " + game.getName() + "! Started by " + C.getFullName(member.getUser()))
        .addPermissionOverride(C.getGuild().getPublicRole(), null, EnumSet.of(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ))
        .addPermissionOverride(member, EnumSet.of(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ), null)
        .complete();
    for (Member curPlayer : players) {
      if (newChannel.getPermissionOverride(curPlayer) == null) {
        newChannel.createPermissionOverride(curPlayer).setAllow(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ).queue();
      } else {
        newChannel.getPermissionOverride(curPlayer).getManager().grant(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ).queue();
      }
    }
    ActiveGame activeGame = new ActiveGame(gameId, (TextChannel) newChannel, C.getGuild().getTextChannelById(newChannel.getId()).createWebhook("game communication").complete(), game, member, players);
    activeGames.put(gameId, activeGame);
    channelToIdMap.put(newChannel.getId(), gameId);
    activeUsers.add(member.getUser().getId());
    activeGame.getGame().gameStart(new GameStartEvent(activeGame));
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
  }
}
