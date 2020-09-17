package io.github.jroy.happybot.game;

import io.github.jroy.happybot.game.games.blank.BlankGame;
import io.github.jroy.happybot.game.games.tictactoe.regular.TicTacToe;
import io.github.jroy.happybot.game.games.tictactoe.ultimate.UltimateTicTacToe;
import io.github.jroy.happybot.util.Roles;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum GameType {

  BLANK(BlankGame.class, "blank", Roles.DEVELOPER, false, false),
  TTT(TicTacToe.class, "Tic-Tac-Toe", Roles.FANS, true, false),
  UTTT(UltimateTicTacToe.class, "Ultimate Tic-Tac-Toe", Roles.FANS, true, true);

  private final Class<? extends Game> gameClass;
  private final String name;
  private final Roles requiredRole;
  private final boolean displayGame;
  private final boolean suportsElo;

  public static boolean isGame(String game) {
    for (GameType curType : GameType.values()) {
      if (curType.name().equals(game)) {
        return true;
      }
    }
    return false;
  }

  public static GameType getGameType(Game game) {
    for (GameType curType : GameType.values()) {
      if (curType.gameClass.isInstance(game)) {
        return curType;
      }
    }
    return BLANK;
  }
}
