package io.github.jroy.happybot.game.games.tictactoe.ultimate;

import io.github.jroy.happybot.game.ActiveGame;
import io.github.jroy.happybot.game.Game;
import io.github.jroy.happybot.game.GameManager;
import io.github.jroy.happybot.game.model.GameMessageReceived;
import io.github.jroy.happybot.game.model.GameReactionReceived;
import io.github.jroy.happybot.game.model.GameStartEvent;
import io.github.jroy.happybot.util.C;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.util.Iterator;

public class UltimateTicTacToe extends Game {
  private UtttGame game;
  public UltimateTicTacToe(GameManager manager) {
    super(manager, "Ultimate Tic-Tac-Toe", "Tic-Tac-Toe on Tic-Tac-Toe", 2, 2, 1000);
  }

  @Override
  protected void gameStart(GameStartEvent event) {
    Iterator<Member> players = event.getActiveGame().getPlayers().iterator();
    game = new UtttGame(players.next().getUser(), players.next().getUser(), event);
  }

  @Override
  protected void messageReceived(GameMessageReceived event) {
    ActiveGame activeGame = event.getActiveGame();

    if (!game.getCurrent().equals(event.getMember().getUser())) {
      return;
    }

    int num;
    try {
      num = Integer.parseUnsignedInt(event.getContent());
      if (num < 1 || num > 9) {
        throw new NumberFormatException();
      }
    } catch (NumberFormatException e) {
      activeGame.sendMessage("You must send a number between 1 and 9.");
      return;
    }

    // change the number from human-readable to machine-readable
    num--;

    if (game.getBoard() < 0) {
      if (game.isFull(num)) {
        activeGame.sendMessage(new EmbedBuilder()
            .setTitle("That board is full!")
            .setDescription(game.getCurrent().getAsMention() + ", select a board.\n" + game.fullRender())
            .setColor(Color.GREEN)
            .build()
        );
        return;
      }
      game.setBoard(num);
      activeGame.sendMessage(new EmbedBuilder()
          .setTitle("Selected the " + game.getName(game.getBoard()) + " board.")
          .setDescription(game.getCurrent().getAsMention() + ", select a point on the " + game.getName(game.getBoard()) + " board.\n" + game.fullRender())
          .setColor(Color.BLUE)
          .build()
      );
      return;
    }

    if (game.makeTurn(num)) {
      User winner = game.getWinner();
      if (winner != null) {
        endGame(activeGame, C.getGuild().getMember(winner));
        return;
      }

      if (game.isFull()) {
        endGame(activeGame, null);
      }

      EmbedBuilder builder = new EmbedBuilder()
          .setTitle("Turn completed.");
      if (game.getBoard() < 0) {
        builder.setDescription(game.getCurrent().getAsMention() + ", select a board.\n" + game.fullRender())
            .setColor(Color.GREEN);
      } else {
        builder.setDescription(game.getCurrent().getAsMention() + ", select a point on the "
            + game.getName(game.getBoard()) + " board.\n"
            + game.fullRender())
            .setColor(Color.BLUE);
      }
      activeGame.sendMessage(builder.build());
    } else {
      activeGame.sendMessage(new EmbedBuilder()
          .setTitle("That space is already occupied.")
          .setDescription(game.getCurrent().getAsMention() + ", select a point on the "
              + game.getName(game.getBoard()) + " board.\n" + game.fullRender())
          .setColor(Color.BLUE)
          .build()
      );
    }
  }

  @Override
  protected void reactionReceived(GameReactionReceived event) {

  }
}
