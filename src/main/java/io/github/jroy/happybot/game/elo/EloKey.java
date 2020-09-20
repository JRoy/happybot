package io.github.jroy.happybot.game.elo;

import io.github.jroy.happybot.game.GameType;
import lombok.Data;

@Data
public class EloKey {
  private final String user;
  private final GameType gameType;
}
