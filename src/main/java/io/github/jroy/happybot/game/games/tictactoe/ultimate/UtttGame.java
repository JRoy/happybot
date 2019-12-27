package io.github.jroy.happybot.game.games.tictactoe.ultimate;

import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import io.github.jroy.happybot.game.games.tictactoe.AbstractTicTacToeGame;
import io.github.jroy.happybot.game.games.tictactoe.TicTacToeBoard;
import io.github.jroy.happybot.game.games.tictactoe.TicTacToeType;
import io.github.jroy.happybot.game.model.GameStartEvent;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;

public class UtttGame extends AbstractTicTacToeGame {
  private TicTacToeBoard[] boards = new TicTacToeBoard[9];
  @Getter
  @Setter
  private int board = -1;

  public UtttGame(User first, User second, GameStartEvent event) {
    super(first, second);
    // initialize board
    for (int i = 0; i < boards.length; i++) {
      boards[i] = new TicTacToeBoard();
    }
    event.getActiveGame().sendMessage(new WebhookEmbedBuilder().setDescription(getCurrent().getAsMention() + ", select a board.\n" + fullRender()).build());
  }

  public boolean makeTurn(int position) {
    if (!boards[board].place(position, turn)) {
      return false;
    }

    if (boards[position].getWinner() != null) {
      board = -1;
    } else {
      board = position;
    }

    changeTurn();
    return true;
  }

  @Override
  public User getWinner() {
    TicTacToeBoard board = new TicTacToeBoard();
    for (int i = 0; i < boards.length; i++) {
      board.place(i, boards[i].getWinner());
    }

    return board.getWinner(first, second);
  }

  public boolean isFull(int board) {
    if (boards[board].getWinner() != null) {
      return true;
    }

    for (TicTacToeType type : boards[board].getBoard()) {
      if (type == null) {
        return false;
      }
    }
    return true;
  }

  public boolean isFull() {
    for (int i = 0; i < boards.length; i++) {
      if (!isFull(i)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String render() {
    StringBuilder render = new StringBuilder();
    for (int board = 0; board < 3; board++) {
      if (board > 0) {
        render.append("━━━━━╋━━━━━╋━━━━━\n");
      }
      for (int line = 0; line < 3; line++) {
        if (line > 0) {
          render.append("─┼─┼─┃─┼─┼─┃─┼─┼─\n");
        }
        render.append(renderBoardLine(board, line));
      }
    }

    return render.toString();
  }

  private String renderBoardLine(int index, int line) {
    StringBuilder render = new StringBuilder();
    int boardStart = index * 3;
    for (int j = boardStart; j < boardStart + 3; j++) {
      TicTacToeType[] types = boards[j].getBoard();
      int start = line * 3;
      for (int i = start; i < start + 3; i++) {
        render.append(types[i] == null ? " " : types[i].getValue())
            .append(i == start + 2 ? j == boardStart + 2 ? "" : "┃" : "│");
      }
    }

    return render.append("\n").toString();
  }
}
