package io.github.jroy.happybot.sql;

import io.github.jroy.happybot.commands.money.ShopReward;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Reward {
  DAILY1(1, "x1.5 Daily Reward", 60000, e -> {
    C.giveRole(e.getMember(), Roles.GAMBLE1, "Added from ^shop reward");
  }),
  DAILY2(2, "x2 Daily Reward", 150000, e -> {
    C.giveRole(e.getMember(), Roles.GAMBLE2, "Added from ^shop reward");
  }),
  CHANNEL(3, "#casino-lounge Channel", 5000, e -> {
    C.giveRole(e.getMember(), Roles.ADDICT, "Added from ^shop reward");
  }),
  STEALTH(4, "Increase Rate of Robbery", 10000, e -> {
  }),
  BAG(5, "Increase Rob Amount", 10000, e -> {
  }),
  COUNTER(6, "(One Time Use) Block a Robbery", 820, e -> {
  });

  private final int id;
  private final String display;
  private final int amount;
  private final ShopReward reward;

  public static boolean containsID(int id) {
    for (Reward curReward : Reward.values()) {
      if (curReward.getId() == id) {
        return true;
      }
    }
    return false;
  }

  public static Reward getFromId(int id) {
    for (Reward curReward : Reward.values()) {
      if (curReward.getId() == id) {
        return curReward;
      }
    }
    return null;
  }
}