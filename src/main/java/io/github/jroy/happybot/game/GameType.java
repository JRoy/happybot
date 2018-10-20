package io.github.jroy.happybot.game;

import io.github.jroy.happybot.game.games.blank.BlankGame;
import io.github.jroy.happybot.game.games.tictactoe.regular.TicTacToe;
import io.github.jroy.happybot.game.games.tictactoe.ultimate.UltimateTicTacToe;
import io.github.jroy.happybot.util.Roles;

public enum GameType {

  BLANK(BlankGame.class, Roles.DEVELOPER),
  TTT(TicTacToe.class, Roles.DEVELOPER),
  UTTT(UltimateTicTacToe.class, Roles.DEVELOPER);

  private Class<?extends Game> gameClass;
  private Roles requiredRole;

  GameType(Class<?extends Game> gameClass, Roles requiredRole) {
    this.gameClass = gameClass;
    this.requiredRole = requiredRole;
  }

  public Class<? extends Game> getGameClass() {
    return gameClass;
  }

  public Roles getRequiredRole() {
    return requiredRole;
  }

  public static boolean isGame(String game) {
    for (GameType curType : GameType.values()) {
      if (curType.name().equals(game)) {
        return true;
      }
    }
    return false;
  }
}
