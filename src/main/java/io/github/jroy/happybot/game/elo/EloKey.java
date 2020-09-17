package io.github.jroy.happybot.game.elo;

import io.github.jroy.happybot.game.GameType;
import lombok.Data;
import net.dv8tion.jda.api.entities.User;

@Data
public class EloKey {
  private final String user;
  private final GameType gameType;
}
