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

  BLANK(BlankGame.class, Roles.DEVELOPER, false),
  TTT(TicTacToe.class, Roles.DEVELOPER, false),
  UTTT(UltimateTicTacToe.class, Roles.FANS, true);

  private final Class<? extends Game> gameClass;
  private final Roles requiredRole;
  private final boolean displayGame;

  public static boolean isGame(String game) {
    for (GameType curType : GameType.values()) {
      if (curType.name().equals(game)) {
        return true;
      }
    }
    return false;
  }
}
