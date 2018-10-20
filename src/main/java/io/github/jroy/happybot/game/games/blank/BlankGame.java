package io.github.jroy.happybot.game.games.blank;

import io.github.jroy.happybot.game.Game;
import io.github.jroy.happybot.game.model.GameMessageReceived;
import io.github.jroy.happybot.game.model.GameReactionReceived;
import io.github.jroy.happybot.game.model.GameStartEvent;

public class BlankGame extends Game {

  public BlankGame() {
    super("Blank", "Debug Game", 1, 1);
  }

  @Override
  protected void gameStart(GameStartEvent event) {
    event.getActiveGame().sendMessage("The game has started!");
  }

  @Override
  protected void messageReceived(GameMessageReceived event) {
    event.getActiveGame().sendMessage("I detected a user message: " + event.getContent());
  }

  @Override
  protected void reactionReceived(GameReactionReceived event) {
    event.getActiveGame().sendMessage("I detected a reaction: " + event.getEvent().getReactionEmote().getName());
  }
}
