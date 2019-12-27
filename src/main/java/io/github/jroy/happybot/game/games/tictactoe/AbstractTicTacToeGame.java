package io.github.jroy.happybot.game.games.tictactoe;

import lombok.Getter;
import net.dv8tion.jda.api.entities.User;

public abstract class AbstractTicTacToeGame {
  @Getter
  protected final User first;
  @Getter
  protected final User second;
  protected TicTacToeType turn;

  public AbstractTicTacToeGame(User first, User second) {
    this.first = first;
    this.second = second;
    this.turn = Math.random() < 0.5 ? TicTacToeType.CROSS : TicTacToeType.NOUGHT;
  }

  protected void changeTurn() {
    turn = (turn == TicTacToeType.CROSS ? TicTacToeType.NOUGHT : TicTacToeType.CROSS);
  }

  public User getCurrent() {
    return turn == TicTacToeType.CROSS ? first : second;
  }

  public User getNext() {
    return turn == TicTacToeType.CROSS ? second : first;
  }

  public String getName(int board) {
    switch(board) {
      case -1:
        return "any";
      case 0:
        return "top left";
      case 1:
        return "top middle";
      case 2:
        return "top right";
      case 3:
        return "middle left";
      case 4:
        return "middle";
      case 5:
        return "middle right";
      case 6:
        return "bottom left";
      case 7:
        return "bottom middle";
      case 8:
        return "bottom right";
      default:
        return null;
    }
  }

  public String fullRender() {
    return "1 = top left, 2 = top middle, 3 = top right,\n" +
        "4 = middle left, 5 = middle, 6 = middle right,\n" +
        "7 = bottom left, 8 = bottom middle, 9 = bottom right.\n" +
        "```\n" + render() + "```";
  }

  public abstract String render();
  public abstract User getWinner();
  public abstract boolean isFull();
  public abstract boolean makeTurn(int position);
}
