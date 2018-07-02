package io.github.jroy.happybot.events.star;

import java.sql.ResultSet;
import java.sql.SQLException;

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

    public long getStarCount() {
        return starCount;
    }

    public long getGildCount() {
        return gildCount;
    }

    public long getHeelCount() {
        return heelCount;
    }
}
