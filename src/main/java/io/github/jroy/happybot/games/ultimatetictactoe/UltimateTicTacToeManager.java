package io.github.jroy.happybot.games.ultimatetictactoe;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class UltimateTicTacToeManager extends ListenerAdapter {
  private final Cache<Long, User> waitingForPlayers = CacheBuilder.newBuilder()
      .expireAfterWrite(5, TimeUnit.MINUTES)
      .build();
  private final Cache<Long, UltimateTicTacToe> games = CacheBuilder.newBuilder()
      .expireAfterAccess(10, TimeUnit.MINUTES)
      .build();

  public User getWaiting(long id) {
    return waitingForPlayers.getIfPresent(id);
  }

  public boolean isWaiting(User user) {
    return waitingForPlayers.asMap().containsValue(user);
  }

  public void putWaiting(long id, User user) {
    waitingForPlayers.put(id, user);
  }

  public UltimateTicTacToe getGame(long id) {
    return games.getIfPresent(id);
  }

  public void updateGames() {
    games.cleanUp();
  }

  @Override
  public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
    long id = event.getMessageIdLong();
    User user = getWaiting(id);
    User sender = event.getUser();
    if (user == null || user.equals(sender) || sender.isBot()) {
      return;
    }
    TextChannel channel = event.getChannel();
    if(games.getIfPresent(event.getChannel().getIdLong()) != null) {
      channel.sendMessage(sender.getAsMention() + ", a game is already in progress!").queue();
      return;
    }
    waitingForPlayers.invalidate(id);

    channel.sendMessage(user.getAsMention() + ", a game has started with " + sender.getAsMention() + "!").queue();

    UltimateTicTacToe game = new UltimateTicTacToe(user, sender);
    games.put(channel.getIdLong(), game);

    channel.sendMessage(new EmbedBuilder()
        .setDescription(game.getCurrent().getAsMention() + ", please select a board.\n" + game.fullRender())
        .setColor(Color.GREEN)
        .build()
    ).queue();
  }

  @Override
  public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
    TextChannel channel = event.getChannel();
    UltimateTicTacToe game = games.getIfPresent(channel.getIdLong());
    // there is no game going on in this channel, we can ignore the message
    if (game == null) {
      return;
    }

    if (!game.getCurrent().equals(event.getAuthor())) {
      return;
    }

    int num;
    try {
      num = Integer.parseUnsignedInt(event.getMessage().getContentRaw());
      if (num < 1 || num > 9) {
        throw new NumberFormatException();
      }
    } catch (NumberFormatException e) {
      channel.sendMessage("You must send a number between 1 and 9.").queue();
      return;
    }

    // change the number from human-readable to machine-readable
    num--;

    if (game.getBoard() < 0) {
      if(game.isFull(num)) {
        channel.sendMessage(new EmbedBuilder()
            .setTitle("That board is full!")
            .setDescription(game.getCurrent().getAsMention() + ", select a board.\n" + game.fullRender())
            .setColor(Color.GREEN)
            .build()
        ).queue();
        return;
      }
      game.setBoard(num);
      channel.sendMessage(new EmbedBuilder()
          .setTitle("Selected the " + game.getBoardName() + " board.")
          .setDescription(game.getCurrent().getAsMention() + ", select a point on the " + game.getBoardName() + " board.\n" + game.fullRender())
          .setColor(Color.BLUE)
          .build()
      ).queue();
      return;
    }

    if (game.makeTurn(num)) {
      User winner = game.getWinner();
      if(winner != null) {
        games.invalidate(channel.getIdLong());
        channel.sendMessage(new EmbedBuilder()
            .setTitle("Winner!")
            .setDescription(winner.getAsMention() + " has won the game of Ultimate Tic Tac Toe!\n```\n" + game.render() + "```")
            .build()
        ).queue();
        return;
      }

      EmbedBuilder builder = new EmbedBuilder()
          .setTitle("Turn completed.");
      if(game.getBoard() < 0) {
        builder.setDescription(game.getCurrent().getAsMention() + ", select a board.\n" + game.fullRender())
            .setColor(Color.GREEN);
      } else {
        builder.setDescription(game.getCurrent().getAsMention() + ", select a point on the " + game.getBoardName() + " board.\n"
            + game.fullRender())
            .setColor(Color.BLUE);
      }
      channel.sendMessage(builder.build()).queue();
    } else {
      channel.sendMessage(new EmbedBuilder()
          .setTitle("That space is already occupied.")
          .setDescription(game.getCurrent().getAsMention() + ", select a point on the " + game.getBoardName() + "board.\n" + game.fullRender())
          .setColor(Color.BLUE)
          .build()
      ).queue();
    }
  }
}
//      │ │ ┃ │ │ ┃ │ │
//     ─┼─┼─╂─┼─┼─╂─┼─┼─
//      │x│ ┃ │ │ ┃ │ │
//     ─┼─┼─╂─┼─┼─╂─┼─┼─
//      │ │ ┃ │ │ ┃ │ │
//     ━━━━━╋━━━━━╋━━━━━
//      │ │ ┃ │ │ ┃ │ │
//     ─┼─┼─╂─┼─┼─╂─┼─┼─
//      │ │ ┃ │ │ ┃ │o│
//     ─┼─┼─╂─┼─┼─╂─┼─┼─
//      │ │ ┃ │ │ ┃ │ │
//     ━━━━━╋━━━━━╋━━━━━
//      │ │ ┃ │ │ ┃ │ │
//     ─┼─┼─╂─┼─┼─╂─┼─┼─
//      │ │ ┃ │ │ ┃ │ │
//     ─┼─┼─╂─┼─┼─╂─┼─┼─
//      │ │ ┃ │ │ ┃ │ │
