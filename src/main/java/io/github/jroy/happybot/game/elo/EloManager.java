package io.github.jroy.happybot.game.elo;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.github.jroy.happybot.game.GameType;
import io.github.jroy.happybot.sql.SQLManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.User;
import org.goochjs.glicko2.Rating;
import org.goochjs.glicko2.RatingCalculator;
import org.goochjs.glicko2.RatingPeriodResults;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class EloManager {
  private static final String CREATE_ELO_TABLE = "CREATE TABLE IF NOT EXISTS `elo` (`userid` VARCHAR(255), `game` VARCHAR(255), `elo` FLOAT, `rd` FLOAT, `velocity` FLOAT, PRIMARY KEY (`userid`, `game`))";
  private static final String GET_PLAYER = "SELECT * FROM `elo` WHERE `userid` = ? AND `game` = ?";
  private static final String GET_RANK = "SELECT *, COUNT(*) FROM (SELECT COUNT(*) AS total, userid, game, elo, RANK() OVER (ORDER BY elo) AS rank FROM elo WHERE game = ?) subquery WHERE userid = ?;";
  private static final String GET_TOP10 = "SELECT userid, elo FROM elo WHERE game = ? ORDER BY elo DESC LIMIT 10;";
  private static final String SET_PLAYER = "REPLACE INTO `elo` (`userid`, `game`, `elo`, `rd`, `velocity`) VALUES (?, ?, ?, ?, ?)";

  private final LoadingCache<EloKey, EloPlayer> eloCache = CacheBuilder.newBuilder()
      .maximumSize(1000)
      .build(new CacheLoader<>() {
        @Override
        public EloPlayer load(@NotNull EloKey key) {
          return loadPlayer(key);
        }
      });

  private final SQLManager sqlManager;
  private final RatingCalculator calculator = new RatingCalculator();

  public EloManager(SQLManager sqlManager) {
    this.sqlManager = sqlManager;

    try {
      sqlManager.getConnection().createStatement().executeUpdate(CREATE_ELO_TABLE);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public EloPlayer getPlayer(EloKey key) {
    try {
      return eloCache.get(key);
    } catch (ExecutionException e) {
      e.printStackTrace();
      return null;
    }
  }

  public List<EloPlayer> getTop10(GameType gameType) {
    try {
      PreparedStatement statement = sqlManager.getConnection().prepareStatement(GET_TOP10);
      statement.setString(1, gameType.name());

      List<EloPlayer> players = new ArrayList<>();

      ResultSet resultSet = statement.executeQuery();
      while (resultSet.next()) {
        players.add(new EloPlayer(resultSet.getString("userid"), gameType, resultSet.getDouble("elo"), 0, 0));
      }
      return players;
    } catch (SQLException e) {
      e.printStackTrace();
      return Collections.emptyList();
    }
  }

  public int getRank(EloKey key) {
    try {
      PreparedStatement statement = sqlManager.getConnection().prepareStatement(GET_RANK);
      statement.setString(1, key.getGameType().name());
      statement.setString(2, key.getUser());

      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        int total = resultSet.getInt("total");
        int rank = resultSet.getInt("rank");

        return rank | (total << 8);
      } else {
        return -1;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return -1;
    }
  }

  private EloPlayer loadPlayer(EloKey key) {
    try {
      PreparedStatement statement = sqlManager.getConnection().prepareStatement(GET_PLAYER);
      statement.setString(1, key.getUser());
      statement.setString(2, key.getGameType().name());

      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        return new EloPlayer(key.getUser(), key.getGameType(), resultSet.getFloat("elo"), resultSet.getFloat("rd"), resultSet.getFloat("velocity"));
      } else {
        return new EloPlayer(key.getUser(), key.getGameType(), 1500, calculator.getDefaultRatingDeviation(), calculator.getDefaultVolatility());
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private void setPlayer(EloPlayer player) {
    eloCache.put(new EloKey(player.getPlayer(), player.getGameType()), player);
    try {
      PreparedStatement statement = sqlManager.getConnection().prepareStatement(SET_PLAYER);
      statement.setString(1, player.getPlayer());
      statement.setString(2, player.getGameType().name());
      statement.setDouble(3, player.getEloRating());
      statement.setDouble(4, player.getRatingDeviation());
      statement.setDouble(5, player.getVolatility());

      statement.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public EloResult calculate(GameType gameType, User winnerUser, User loserUser, boolean draw) {
    EloPlayer winnerStart = getPlayer(new EloKey(winnerUser.getId(), gameType));
    EloPlayer loserStart = getPlayer(new EloKey(loserUser.getId(), gameType));
    if (winnerStart == null || loserStart == null) {
      return null;
    }

    Rating winner = winnerStart.toRating(calculator);
    Rating loser = loserStart.toRating(calculator);

    Set<Rating> set = new HashSet<>();
    set.add(winner);
    set.add(loser);
    RatingPeriodResults results = new RatingPeriodResults(set);
    if (draw) {
      results.addDraw(winner, loser);
    } else {
      results.addResult(winner, loser);
    }
    calculator.updateRatings(results);

    setPlayer(EloPlayer.fromRating(winner, gameType));
    setPlayer(EloPlayer.fromRating(loser, gameType));

    return new EloResult(
        winnerUser, (int) Math.round(winner.getRating()), (int) Math.round(winner.getRating() - winnerStart.getEloRating()),
        loserUser, (int) Math.round(loser.getRating()), (int) Math.round(loser.getRating() - loserStart.getEloRating()));
  }
}
