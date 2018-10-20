package io.github.jroy.happybot.events.star;

import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
public class StarStatsToken {
  private final long starCount;
  private final long gildCount;
  private final long heelCount;

  StarStatsToken(ResultSet resultSet) throws SQLException {
    resultSet.next();
    starCount = resultSet.getLong("stars");
    gildCount = resultSet.getLong("gilds");
    heelCount = resultSet.getLong("heels");
  }
}
