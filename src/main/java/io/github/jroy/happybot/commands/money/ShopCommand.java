package io.github.jroy.happybot.commands.money;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.sql.SQLManager;
import io.github.jroy.happybot.sql.UserToken;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Constants;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.core.EmbedBuilder;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;

public class ShopCommand extends CommandBase {
  private static final String currentShopHelp = "**Happyheart Shop Help**\n" +
      "This shop allows for you to spend your money on\n" +
      "stuff. To view the products we offer please do\n" +
      "`^shop items` to get the list of the shop items.\n" +
      "If you would like to buy an item do the following:\n" +
      "`^shop buy <id>` This will buy it from your account!";

  private SQLManager sqlManager;


    public ShopCommand(SQLManager sqlManager) {
        super("shop", "<page/buy/help>", "Fun activity thing let's you do things.", CommandCategory.FUN);
        this.sqlManager = sqlManager;
    }

    @Override
    protected void executeCommand(CommandEvent e) {
        if (e.getArgs().isEmpty()) {
            e.replyError(currentShopHelp);
            return;
        }
        if (e.getArgs().equalsIgnoreCase("items")) {
            EmbedBuilder builder = new EmbedBuilder()
                    .setTitle("Coin Shop")
                    .setDescription("Here are the items for sale in the shop at this point in time.");
            for (Rewards reward : Rewards.values()) {
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
            if (!Rewards.containsID(selectedID)) {
                e.replyError(C.bold("Correct Usage:") + " ^shop buy **<id>**");
                return;
            }

            //Grab Desired Item
            Rewards reward = Rewards.getFromId(selectedID);

            if (reward == null) {
                e.replyError("There was an error while handling your purchase. You have not been charged. [Null Reward]");
                return;
            }

            if (!sqlManager.isActiveUserH(e.getMember().getUser().getId())) {
                e.replyError(MoneyCommand.NEED_ACCOUNT);
                return;
            }

            try {
                UserToken userToken = sqlManager.getUser(e.getMember().getUser().getId());
                if (userToken.getCoins() < reward.getAmount()) {
                    e.replyError("You do not have the required funds to complete this purchase!");
                    return;
                }
                if (reward == Rewards.CHANNEL) {
                    userToken.takeCoins(reward.getAmount());
                    C.giveRole(e.getMember(), Roles.ADDICT, "Added from ^shop reward");
                    e.replySuccess("You have purchased the `" + reward.getDisplay() + "` Reward!");
                } else if (reward == Rewards.DAILY1) {
                    userToken.takeCoins(reward.getAmount());
                    C.giveRole(e.getMember(), Roles.GAMBLE1, "Added from ^shop reward");
                    e.replySuccess("You have purchased the `" + reward.getDisplay() + "` Reward!");
                } else if (reward == Rewards.DAILY2) {
                    userToken.takeCoins(reward.getAmount());
                    C.giveRole(e.getMember(), Roles.GAMBLE2, "Added from ^shop reward");
                    e.replySuccess("You have purchased the `" + reward.getDisplay() + "` Reward!");
                } else if (reward == Rewards.GAME) {
                    userToken.takeCoins(reward.getAmount());
                    C.privChannel(C.getGuild().getMemberById(Constants.HAPPYHEART_DISCORD_ID.get()), "You have to play a bedwars game with: " + e.getMember().getAsMention()+ ". They bought it so ya lel.");
                    e.reply("You have purchased the `" + reward.getDisplay() + "` Reward! Happy will contact you soon!");
                } else {
                    e.replyError("There was an error while handling your purchase. You have not been charged. [Unhandled Reward]");
                }
            } catch (SQLException e1) {
                e.replyError("There was an error while handling your purchase. You have not been charged. [SQL Error]");
            }


        }
    }

    private enum Rewards {
        DAILY1(1, "x1.5 Daily Reward", 60000),
        DAILY2(2, "x2 Daily Reward", 150000),
        CHANNEL(3, "#casino-lounge Channel", 5000),
        GAME(4, "A game of bedwars with happyheart", 1000000);

        private int id;
        private String display;
        private int amount;

        Rewards(int id, String display, int amount) {
            this.id = id;
            this.display = display;
            this.amount = amount;
        }

        public static boolean containsID(int id) {
            for (Rewards curReward: Rewards.values()) {
                if (curReward.getId() == id) {
                    return true;
                }
            }
            return false;
        }

        public static Rewards getFromId(int id) {
            for (Rewards curReward : Rewards.values()) {
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
    }

}
