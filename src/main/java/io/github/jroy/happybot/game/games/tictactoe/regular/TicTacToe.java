package io.github.jroy.happybot.game.games.tictactoe.regular;

import io.github.jroy.happybot.game.Game;
import io.github.jroy.happybot.game.GameManager;
import io.github.jroy.happybot.game.model.GameMessageReceived;
import io.github.jroy.happybot.game.model.GameReactionReceived;
import io.github.jroy.happybot.game.model.GameStartEvent;

public class TicTacToe extends Game {

  public TicTacToe(GameManager manager) {
    super(manager, "Tic-Tac-Toe", "A classic game of Tic-Tac-Toe", 2, 2, 500);
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
