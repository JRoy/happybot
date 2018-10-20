package io.github.jroy.happybot.game;

import io.github.jroy.happybot.game.model.GameMessageReceived;
import io.github.jroy.happybot.game.model.GameReactionReceived;
import io.github.jroy.happybot.game.model.GameStartEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class Game {
  private final GameManager manager;
  private final String name;
  private final String description;
  private final int minPlayers;
  private final int maxPlayers;

  protected abstract void gameStart(GameStartEvent event);

  protected abstract void messageReceived(GameMessageReceived event);

  protected abstract void reactionReceived(GameReactionReceived event);
}
