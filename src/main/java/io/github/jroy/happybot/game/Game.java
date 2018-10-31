package io.github.jroy.happybot.game;

import io.github.jroy.happybot.game.model.GameMessageReceived;
import io.github.jroy.happybot.game.model.GameReactionReceived;
import io.github.jroy.happybot.game.model.GameStartEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.entities.Member;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
@Getter
public abstract class Game {
  private final GameManager manager;
  private final String name;
  private final String description;
  private final int minPlayers;
  private final int maxPlayers;
  private final int coinPrize;

  protected abstract void gameStart(GameStartEvent event);

  protected abstract void messageReceived(GameMessageReceived event);

  protected abstract void reactionReceived(GameReactionReceived event);

  /**
   * Ends a game and rewards prize to winner.
   * @param activeGame The active game instance to call the winner from.
   * @param winner Winner of the game. Can be null to indicate no winner.
   */
  protected void endGame(ActiveGame activeGame, @Nullable Member winner) {
    manager.protectGame(activeGame, winner, coinPrize);
  }

}
