package io.github.jroy.happybot.game.games.tictactoe;

import lombok.Getter;
import net.dv8tion.jda.core.entities.User;

import static io.github.jroy.happybot.game.games.tictactoe.TicTacToeType.CROSS;

public class TicTacToeBoard {
  @Getter
  private TicTacToeType winner;
  private TicTacToeType[] board = new TicTacToeType[9];

  public TicTacToeType[] getBoard() {
    if(winner == CROSS) {
      return new TicTacToeType[] {
          CROSS, null, CROSS,
          null, CROSS, null,
          CROSS, null, CROSS
      };
    } else if(winner == TicTacToeType.NOUGHT) {
      return new TicTacToeType[] {
          TicTacToeType.NOUGHT, TicTacToeType.NOUGHT, TicTacToeType.NOUGHT,
          TicTacToeType.NOUGHT, null, TicTacToeType.NOUGHT,
          TicTacToeType.NOUGHT, TicTacToeType.NOUGHT, TicTacToeType.NOUGHT
      };
    }
    return board;
  }

  public User getWinner(User first, User second) {
    if (winner == null) {
      return null;
    } else {
      return winner == CROSS ? first : second;
    }
  }

  /**
   * Places a given type onto the board at the given inde
   * @param index the index to place the {@link TicTacToeType} at
   * @param type the type of piece
   * @return whether the placement was successful.
   */
  public boolean place(int index, TicTacToeType type) {
    if(board[index] != null) {
      return false;
    }

    board[index] = type;
    checkForWinner();
    return true;
  }

  public boolean isFull() {
    for (TicTacToeType type : board) {
      if (type == null) {
        return false;
      }
    }
    return true;
  }

  private void checkForWinner() {
    // horizontal lines
    for(int i = 0; i < 3; i++) {
      int j = i * 3;
      if(board[j] != null && board[j] == board[j + 1] && board[j] == board[j + 2]) {
        winner = board[j];
        return;
      }
    }

    // vertical lines
    for(int i = 0; i < 3; i++) {
      if(board[i] != null && board[i] == board[i + 3] && board[i] == board[i + 6]) {
        winner = board[i];
        return;
      }
    }

    // diagonals
    if(board[0] != null && board[0] == board[4] && board[0] == board[8]) {
      winner = board[0];
    } else if(board[2] != null && board[2] == board[4] && board[2] == board[6]) {
      winner = board[2];
    }
  }
}