package io.github.jroy.happybot.games.blank;

import io.github.jroy.happybot.games.Game;
import io.github.jroy.happybot.games.model.GameMessageReceived;

public class BlankGame extends Game {

  public BlankGame() {
    super("Blank", "This game serves no use but for testing.", 1, 1);
  }

  @Override
  protected void messageReceived(GameMessageReceived event) {

  }
}
