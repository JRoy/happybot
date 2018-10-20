package io.github.jroy.happybot.games.ultimatetictactoe;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TicTacToeType {
  CROSS("x"),
  NOUGHT("o");

  @Getter
  private final String value;
}
