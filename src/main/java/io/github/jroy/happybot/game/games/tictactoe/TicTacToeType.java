package io.github.jroy.happybot.game.games.tictactoe;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TicTacToeType {
  CROSS("x"),
  NOUGHT("o");

  @Getter
  private final String value;
}
