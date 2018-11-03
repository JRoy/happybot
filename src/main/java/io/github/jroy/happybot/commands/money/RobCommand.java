package io.github.jroy.happybot.commands.money;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.sql.PurchaseManager;
import io.github.jroy.happybot.sql.Reward;
import io.github.jroy.happybot.sql.SQLManager;
import io.github.jroy.happybot.sql.UserToken;
import io.github.jroy.happybot.util.C;
import net.dv8tion.jda.core.entities.Member;

import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class RobCommand extends CommandBase {

  private final static int FINE = 300;
  private final static int SECURITY_MULTIPLIER = 2;
  private final HashMap<String, RobToken> robTokens = new HashMap<>();
  private SQLManager sqlManager;
  private PurchaseManager purchaseManager;

  public RobCommand(PurchaseManager purchaseManager) {
    super("rob", "<user>", "Robs a user for their money.", CommandCategory.FUN);
    this.purchaseManager = purchaseManager;
    this.sqlManager = purchaseManager.getSqlManager();
    setCooldown(30, ChronoUnit.MINUTES);
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    Member target = null;
    if (!e.getArgs().isEmpty()) {
      Member fromName = C.getMemberFromName(e.getArgs());
      if (e.containsMention()) {
        target = e.getMentionedMember();
      } else if (fromName != null) {
        target = fromName;
      }
    }
    if (target == null) {
      e.reply(invalid);
      removeFromCooldown(e.getMember());
      return;
    }
    try {
      // Check both members have a money account
      String userId = e.getMember().getUser().getId();
      if (!sqlManager.isActiveUserH(userId) || !sqlManager.isActiveUserH(target.getUser().getId())) {
        e.reply("Both you and the person you are attempting to rob must have a money account!");
        removeFromCooldown(e.getMember());
        return;
      }

      // Check the user is not robbing themselves
      if (userId.equalsIgnoreCase(target.getUser().getId())) {
        e.reply("Why would you rob yourself?");
        removeFromCooldown(e.getMember());
        return;
      }

      robTokens.putIfAbsent(userId, new RobToken());
      RobToken token = robTokens.get(userId);

      // Check the user has not robbed in the last day
	    int secondsRemaining = token.getTimeRemainingForUser(userId);
      if (secondsRemaining > 0) {
	      e.replyError("You may only rob a user once a day! You can rob them again in "
            + C.format(secondsRemaining, TimeUnit.SECONDS, TimeUnit.MINUTES));
        removeFromCooldown(e.getMember());
        return;
      }

      UserToken userToken = sqlManager.getUser(userId);
      UserToken targetToken = sqlManager.getUser(target.getUser().getId());

      if (targetToken.getCoins() < 1000) {
        e.reply("This person has under 1000 coins, give them a break!");
        removeFromCooldown(e.getMember());
        return;
      } else if (userToken.getCoins() < FINE * SECURITY_MULTIPLIER) {
        e.reply("You don't have enough coins to be worth it, you'll go broke!\n" +
            "You need " + FINE * SECURITY_MULTIPLIER + " coins.");
        return;
      }

      int origin = 100;
      int bound = 500;
      if (purchaseManager.hasReward(userId, Reward.BAG)) {
        origin = 200;
        bound = 700;
      }

      int robAmount = ThreadLocalRandom.current().nextInt(origin, bound);
      if (targetToken.getCoins() < robAmount) {
        e.reply("The person you are trying to steal from does not have any money to steal! You got caught in the act!\n" +
            "    -" + FINE + " coins.");
        token.registerRob(target.getUser().getId());
        userToken.takeCoins(FINE);
        return;
      }

      if (purchaseManager.hasReward(target.getUser().getId(), Reward.COUNTER)) {
        purchaseManager.deleteSingleReward(target.getUser().getId(), Reward.COUNTER);
        targetToken.addCoins(FINE * SECURITY_MULTIPLIER);
        userToken.takeCoins(FINE * SECURITY_MULTIPLIER);
        e.replyError("!!SECURITY SYSTEM TRIGGERED!! You must pay " + FINE * SECURITY_MULTIPLIER + " coins to get out of jail!");
        return;
      }

      double rate = 0.5;
      if (purchaseManager.hasReward(userId, Reward.STEALTH)) {
        rate = 0.70;
      }

      double chance = Math.random();
      if (chance < rate) {
        targetToken.takeCoins(robAmount);
        userToken.addCoins(robAmount);
        token.registerRob(target.getUser().getId());
        e.reply("Hey " + target.getAsMention() + ", you just got robbed by " + e.getMember().getAsMention() + " for " + robAmount + " coins!");
      } else {
        e.reply(e.getMember().getAsMention() + ", the feds caught you in the act you thief.\n" +
            "    -" + FINE + " coins as fine.");
        token.registerRob(target.getUser().getId());
        userToken.takeCoins(Math.min(FINE, userToken.getCoins()));
      }
    } catch (SQLException e1) {
      e.reply("Oof Error: " + e1.getMessage());
      removeFromCooldown(e.getMember());

    }
  }
}
