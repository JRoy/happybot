package io.github.jroy.happybot.commands.money;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.sql.PurchaseManager;
import io.github.jroy.happybot.sql.Reward;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class ReclaimCommand extends CommandBase {

  private final PurchaseManager purchaseManager;

  public ReclaimCommand(PurchaseManager purchaseManager) {
    super("reclaim", null, "Reclaim the items you've purchased via the store.", CommandCategory.FUN);
    this.purchaseManager = purchaseManager;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    try {
      ResultSet rewards = purchaseManager.getAllRewards(e.getMember().getUser().getId());
      while (rewards.next()) {
        Objects.requireNonNull(Reward.getFromId(rewards.getInt("itemId"))).getReward().processReward(e);
      }
      e.reply("Applied all your rewards!");
    } catch (SQLException e1) {
      e.replyError("Oof Error: " + e1.getMessage());
    }
  }
}
