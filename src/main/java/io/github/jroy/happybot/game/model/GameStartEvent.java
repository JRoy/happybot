package io.github.jroy.happybot.game.model;

import io.github.jroy.happybot.game.ActiveGame;

public class GameStartEvent {

  private ActiveGame activeGame;

  public GameStartEvent(ActiveGame activeGame) {
    this.activeGame = activeGame;
  }

  public ActiveGame getActiveGame() {
    return activeGame;
  }
}
