package io.github.jroy.happybot.commands.money;

import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.sql.PurchaseManager;
import io.github.jroy.happybot.sql.Reward;
import io.github.jroy.happybot.sql.UserToken;

import java.sql.SQLException;

public interface ShopReward {

    default void buyReward(UserToken token, PurchaseManager purchaseManager, CommandEvent e, Reward reward) throws SQLException {
        token.takeCoins(reward.getAmount());
        purchaseManager.addReward(e.getMember().getUser().getId(), reward);
        processReward(e);
        e.replySuccess("You have purchased the `" + reward.getDisplay() + "` Reward!");
    }

    void processReward(CommandEvent e) throws SQLException;

}
