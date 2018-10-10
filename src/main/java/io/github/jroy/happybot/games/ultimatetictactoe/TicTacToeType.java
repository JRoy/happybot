package io.github.jroy.happybot.games.ultimatetictactoe;

public enum  TicTacToeType {
  CROSS("x"),
  NOUGHT("o");

  private String value;

  TicTacToeType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
