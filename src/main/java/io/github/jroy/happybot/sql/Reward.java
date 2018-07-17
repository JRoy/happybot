package io.github.jroy.happybot.sql;

import io.github.jroy.happybot.commands.money.ShopReward;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;

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
    STEALTH(4, "Increase Rate of Robbery", 10000, e -> {}),
    BAG(5, "Increase Rob Amount", 10000, e -> {}),
    COUNTER(6, "(One Time Use) Block a Robbery", 2100, e -> {});

    private int id;
    private String display;
    private int amount;
    private ShopReward reward;

    Reward(int id, String display, int amount, ShopReward reward) {
        this.id = id;
        this.display = display;
        this.amount = amount;
        this.reward = reward;
    }

    public static boolean containsID(int id) {
        for (Reward curReward: Reward.values()) {
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

    public int getId() {
        return id;
    }

    public String getDisplay() { return display; }

    public int getAmount() {
        return amount;
    }

    public ShopReward getReward() {
        return reward;
    }
}