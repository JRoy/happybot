package io.github.jroy.happybot.commands.money;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.sql.PurchaseManager;
import io.github.jroy.happybot.sql.Reward;
import io.github.jroy.happybot.sql.UserToken;
import io.github.jroy.happybot.util.C;
import net.dv8tion.jda.core.EmbedBuilder;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;

public class ShopCommand extends CommandBase {
  private static final String CURRENT_SHOP_HELP = "**Happyheart Shop Help**\n" +
      "This shop allows for you to spend your money on\n" +
      "stuff. To view the products we offer please do\n" +
      "`^shop items` to get the list of the shop items.\n" +
      "If you would like to buy an item do the following:\n" +
      "`^shop buy <id>` This will buy it from your account!";

  private PurchaseManager purchaseManager;


    public ShopCommand(PurchaseManager purchaseManager) {
        super("shop", "<page/buy/help>", "Fun activity thing let's you do things.", CommandCategory.FUN);
        this.purchaseManager = purchaseManager;
    }

    @Override
    protected void executeCommand(CommandEvent e) {
        if (e.getArgs().isEmpty()) {
            e.replyError(CURRENT_SHOP_HELP);
            return;
        }
        if (e.getArgs().equalsIgnoreCase("items")) {
            EmbedBuilder builder = new EmbedBuilder()
                    .setTitle("Coin Shop")
                    .setDescription("Here are the items for sale in the shop at this point in time.");
            for (Reward reward : Reward.values()) {
                builder.addField("#" + reward.getId() + " " + reward.getDisplay(), C.prettyNum(reward.getAmount()), false);
            }
            e.reply(builder.build());
        } else if (e.getArgs().startsWith("buy")) {
            String id = e.getArgs().replaceFirst("buy ", "");
            if (!StringUtils.isNumeric(id)) {
                e.replyError(C.bold("Correct Usage:") + " ^shop buy **<id>**");
                return;
            }
            int selectedID = Integer.parseInt(id);
            if (!Reward.containsID(selectedID)) {
                e.replyError(C.bold("Correct Usage:") + " ^shop buy **<id>**");
                return;
            }

            //Grab Desired Item
            Reward reward = Reward.getFromId(selectedID);

            if (reward == null) {
                e.replyError("There was an error while handling your purchase. You have not been charged. [Null Reward]");
                return;
            }

            if (!purchaseManager.getSqlManager().isActiveUserH(e.getMember().getUser().getId())) {
                e.replyError(MoneyCommand.NEED_ACCOUNT);
                return;
            }

            try {
                UserToken userToken = purchaseManager.getSqlManager().getUser(e.getMember().getUser().getId());
                if (purchaseManager.hasReward(e.getMember().getUser().getId(), reward)) {
                    e.replyError("You already own this item!");
                    return;
                }
                if (userToken.getCoins() < reward.getAmount()) {
                    e.replyError("You do not have the required funds to complete this purchase!");
                    return;
                }
                reward.getReward().buyReward(userToken, purchaseManager, e, reward);
            } catch (SQLException e1) {
                e.replyError("There was an error while handling your purchase. You have not been charged. [SQL Error]");
            }


        }
    }
}
