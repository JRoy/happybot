package io.github.jroy.happybot.game.games.tictactoe.ultimate;

import io.github.jroy.happybot.game.Game;
import io.github.jroy.happybot.game.model.GameMessageReceived;
import io.github.jroy.happybot.game.model.GameReactionReceived;
import io.github.jroy.happybot.game.model.GameStartEvent;

public class UltimateTicTacToe extends Game {

  public UltimateTicTacToe() {
    super("Ultimate Tic-Tac-Toe", "Tic-Tac-Toe cranked to level 100", 2, 2);
  }

  @Override
  protected void gameStart(GameStartEvent event) {

  }

  @Override
  protected void messageReceived(GameMessageReceived event) {

  }

  @Override
  protected void reactionReceived(GameReactionReceived event) {

  }
}
