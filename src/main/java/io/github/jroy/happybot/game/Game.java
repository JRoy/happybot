package io.github.jroy.happybot.game;

import io.github.jroy.happybot.game.model.GameMessageReceived;
import io.github.jroy.happybot.game.model.GameReactionReceived;
import io.github.jroy.happybot.game.model.GameStartEvent;

public abstract class Game {

  private String name;
  private String description;
  private int minPlayers;
  private int maxPlayers;

  public Game(String name, String description, int minPlayers, int maxPlayers) {
    this.name = name;
    this.description = description;
    this.minPlayers = minPlayers;
    this.maxPlayers = maxPlayers;
  }

  protected abstract void gameStart(GameStartEvent event);

  protected abstract void messageReceived(GameMessageReceived event);

  protected abstract void reactionReceived(GameReactionReceived event);

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public int getMinPlayers() {
    return minPlayers;
  }

  public int getMaxPlayers() {
    return maxPlayers;
  }
}
