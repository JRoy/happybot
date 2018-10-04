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

  protected RobToken() {

  }

  protected void registerRob(String userId) {
    dailyTimes.put(userId, OffsetDateTime.now().plusSeconds(ROB_DELAY));
  }

  /**
   * Gets the time remaining before the user can rob again in seconds
   * Returns -1 if the user can rob immediately
   */
  protected int getTimeRemainingForUser(String userId) {
  	if (!dailyTimes.containsKey(userId)) {
  		return -1;
    } else {
	    return (int) OffsetDateTime.now().until(dailyTimes.get(userId), ChronoUnit.SECONDS);
    }
  }

}
