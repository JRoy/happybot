package io.github.jroy.happybot.game.games.tictactoe.regular;

import io.github.jroy.happybot.game.games.tictactoe.AbstractTicTacToeGame;
import io.github.jroy.happybot.game.games.tictactoe.TicTacToeBoard;
import io.github.jroy.happybot.game.games.tictactoe.TicTacToeType;
import net.dv8tion.jda.api.entities.User;

public class TicTacToeGame extends AbstractTicTacToeGame {
  private final TicTacToeBoard board = new TicTacToeBoard();

  public TicTacToeGame(User first, User second) {
    super(first, second);
  }

  @Override
  public String render() {
    StringBuilder render = new StringBuilder();
    for (int line = 0; line < 3; line++) {
      if (line > 0) {
        render.append("\n─┼─┼─\n");
      }
      for (int i = 0; i < 3; i++) {
        if (i > 0) {
          render.append("│");
        }

        TicTacToeType type = board.getBoard()[line * 3 + i];
        render.append(type == null ? " " : type.getValue());
      }
    }
    return render.toString();
  }

  @Override
  public User getWinner() {
    return board.getWinner(first, second);
  }

  @Override
  public boolean isFull() {
    return board.isFull();
  }

  @Override
  public boolean makeTurn(int position) {
    if (!board.place(position, turn)) {
      return false;
    }

    changeTurn();
    return true;
  }
}
