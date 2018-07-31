package io.github.jroy.happybot.commands.money;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

@SuppressWarnings("FieldCanBeLocal")
class RobToken {

  /**
   * Robbed ID & Time Until Can Rob Again
   */
  private final HashMap<String, OffsetDateTime> dailyTimes = new HashMap<>();
  private final int ROB_DELAY = 86400000; //1 rob per day per person

  RobToken() {

  }

  void registerRob(String userId) {
    dailyTimes.put(userId, OffsetDateTime.now().plusSeconds(ROB_DELAY));
  }

  boolean canRob(String userId) {
    return !dailyTimes.containsKey(userId) || getTimeRemainingForUser(userId) <= 0;
  }

  private int getTimeRemainingForUser(String userId) {
    return (int) OffsetDateTime.now().until(dailyTimes.get(userId), ChronoUnit.SECONDS);
  }

}
