package io.github.jroy.happybot.game.elo;

import io.github.jroy.happybot.game.GameType;
import lombok.Data;
import org.goochjs.glicko2.Rating;
import org.goochjs.glicko2.RatingCalculator;

@Data
public class EloPlayer {
  private final String player;
  private final GameType gameType;
  private final double eloRating;
  private final double ratingDeviation;
  private final double volatility;

  public Rating toRating(RatingCalculator calculator) {
    return new Rating(player, calculator, eloRating, ratingDeviation, volatility);
  }

  public static EloPlayer fromRating(Rating rating, GameType gameType) {
    return new EloPlayer(rating.getUid(), gameType, rating.getRating(), rating.getRatingDeviation(), rating.getVolatility());
  }
}
