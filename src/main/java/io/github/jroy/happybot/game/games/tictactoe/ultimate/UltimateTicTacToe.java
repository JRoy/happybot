package io.github.jroy.happybot.game.games.tictactoe.ultimate;

import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import io.github.jroy.happybot.game.ActiveGame;
import io.github.jroy.happybot.game.Game;
import io.github.jroy.happybot.game.GameManager;
import io.github.jroy.happybot.game.model.GameMessageReceived;
import io.github.jroy.happybot.game.model.GameReactionReceived;
import io.github.jroy.happybot.game.model.GameStartEvent;
import io.github.jroy.happybot.util.C;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

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
        activeGame.sendMessage(new WebhookEmbedBuilder()
            .setTitle(new WebhookEmbed.EmbedTitle("That board is full!", null))
            .setDescription(game.getCurrent().getAsMention() + ", select a board.\n" + game.fullRender())
            .setColor(Color.GREEN.getRGB())
            .build()
        );
        return;
      }
      game.setBoard(num);
      activeGame.sendMessage(new WebhookEmbedBuilder()
          .setTitle(new WebhookEmbed.EmbedTitle("Selected the " + game.getName(game.getBoard()) + " board.", null))
          .setDescription(game.getCurrent().getAsMention() + ", select a point on the " + game.getName(game.getBoard()) + " board.\n" + game.fullRender())
          .setColor(Color.BLUE.getRGB())
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

      WebhookEmbedBuilder builder = new WebhookEmbedBuilder()
          .setTitle(new WebhookEmbed.EmbedTitle("Turn completed.", null));
      if (game.getBoard() < 0) {
        builder.setDescription(game.getCurrent().getAsMention() + ", select a board.\n" + game.fullRender())
            .setColor(Color.GREEN.getRGB());
      } else {
        builder.setDescription(game.getCurrent().getAsMention() + ", select a point on the "
            + game.getName(game.getBoard()) + " board.\n"
            + game.fullRender())
            .setColor(Color.BLUE.getRGB());
      }
      activeGame.sendMessage(builder.build());
    } else {
      activeGame.sendMessage(new WebhookEmbedBuilder()
          .setTitle(new WebhookEmbed.EmbedTitle("That space is already occupied.", null))
          .setDescription(game.getCurrent().getAsMention() + ", select a point on the "
              + game.getName(game.getBoard()) + " board.\n" + game.fullRender())
          .setColor(Color.BLUE.getRGB())
          .build()
      );
    }
  }

  @Override
  protected void reactionReceived(GameReactionReceived event) {

  }
}
