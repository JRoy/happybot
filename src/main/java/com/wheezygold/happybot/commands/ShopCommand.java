package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.wheezygold.happybot.sql.SQLManager;
import com.wheezygold.happybot.sql.UserToken;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Constants;
import com.wheezygold.happybot.util.Roles;
import net.dv8tion.jda.core.EmbedBuilder;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;

@SuppressWarnings("FieldCanBeLocal")
public class ShopCommand extends Command {

    private SQLManager sqlManager;

    private String currentShopHelp = "**Happyheart Shop Help**\n" +
            "This shop allows for you to spend your money on\n" +
            "stuff. To view the products we offer please do\n" +
            "`^shop items` to get the list of the shop items.\n" +
            "If you would like to buy an item do the following:\n" +
            "`^shop buy <id>` This will buy it from your account!";

    public ShopCommand(SQLManager sqlManager) {
        this.name = "shop";
        this.help = "Fun activity thing let's you do things.";
        this.arguments = "<page/buy/help>";
        this.guildOnly = true;
        this.category = new Category("Fun");
        this.sqlManager = sqlManager;
    }

    @Override
    protected void execute(CommandEvent e) {
        if (e.getArgs().isEmpty()) {
            e.replyError(currentShopHelp);
            return;
        }
        if (e.getArgs().equalsIgnoreCase("items")) {
            EmbedBuilder builder = new EmbedBuilder()
                    .setTitle("Coin Shop")
                    .setDescription("Here are the items for sale in the shop at this point in time.");
            for (Rewards reward : Rewards.values()) {
                builder.addField("#" + String.valueOf(reward.getId()) + " " + reward.getDisplay(), C.prettyNum(reward.getAmount()), false);
            }
            e.reply(builder.build());
        } else if (e.getArgs().startsWith("buy")) {
            String id = e.getArgs().replaceAll("(buy )", "");
            if (!StringUtils.isNumeric(id)) {
                e.replyError("**Correct Usage:** ^shop buy **<id>**");
                return;
            }
            int selectedID = Integer.parseInt(id);
            if (!Rewards.containsID(selectedID)) {
                e.replyError("**Correct Usage:** ^shop buy **<id>**");
                return;
            }

            //Grab Desired Item
            Rewards reward = Rewards.getFromId(selectedID);

            if (reward == null) {
                e.replyError("There was an error while handling your purchase. You have not been charged. [Null Reward]");
                return;
            }

            if (!sqlManager.isActiveUserH(e.getMember().getUser().getId())) {
                e.replyError("You do not have an account! Please run `^money create` to make one!");
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
