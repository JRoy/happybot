package io.github.jroy.happybot.games.ultimatetictactoe;

import net.dv8tion.jda.core.entities.User;

public class UltimateTicTacToe {
  private final User first;
  private final User second;
  private TicTacToeBoard[] boards = new TicTacToeBoard[9];
  private TicTacToeType turn;
  private int board = -1;
  private boolean firstTurn = true;

  public UltimateTicTacToe(User first, User second) {
    this.first = first;
    this.second = second;
    this.turn = Math.random() < 0.5 ? TicTacToeType.CROSS : TicTacToeType.NOUGHT;
    // initialize board
    for(int i = 0; i < boards.length; i++) {
      boards[i] = new TicTacToeBoard();
    }
  }

  public String getBoardName() {
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

  public boolean isFirstTurn() {
    return firstTurn;
  }

  public User getCurrent() {
    return turn == TicTacToeType.CROSS ? first : second;
  }

  public User getNext() {
    return turn == TicTacToeType.CROSS ? second : first;
  }

  public int getBoard() {
    return board;
  }

  public void setBoard(int board) {
    if(board < 0 || board > 8) {
      throw new IllegalArgumentException("board is not in bounds");
    }
    this.board = board;
  }

  public boolean makeTurn(int position) {
    if(!boards[board].place(position, turn)) {
      return false;
    }

    if(boards[position].getWinner() != null) {
      board = -1;
    } else {
      board = position;
    }
    firstTurn = false;
    turn = (turn == TicTacToeType.CROSS ? TicTacToeType.NOUGHT : TicTacToeType.CROSS);
    return true;
  }

  public String fullRender() {
    return "1 = top left, 2 = top middle, 3 = top right,\n" +
        "4 = middle left, 5 = middle, 6 = middle right,\n" +
        "7 = bottom left, 8 = bottom middle, 9 = bottom right.\n" +
        "```\n" + render() + "```";
  }

  public String render() {
    StringBuilder render = new StringBuilder();
    for(int board = 0; board < 3; board++) {
      if (board > 0) {
        render.append("━━━━━╋━━━━━╋━━━━━\n");
      }
      for(int line = 0; line < 3; line++) {
        if(line > 0) {
          render.append("─┼─┼─╂─┼─┼─╂─┼─┼─\n");
        }
        render.append(renderBoardLine(board, line));
      }
    }

    return render.toString();
  }

  private String renderBoardLine(int index, int line) {
    StringBuilder render = new StringBuilder();
    int boardStart = index * 3;
    for(int j = boardStart; j < boardStart + 3; j++ ) {
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
