package io.github.jroy.happybot.game.elo;

import lombok.Data;
import net.dv8tion.jda.api.entities.User;

@Data
public class EloResult {
  private final User winner;
  private final int winnerElo;
  private final int winnerEloChange;

  private final User loser;
  private final int loserElo;
  private final int loserEloChange;
}
