package io.github.jroy.happybot.commands.money;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.sql.SQLManager;
import io.github.jroy.happybot.sql.UserToken;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntUnaryOperator;

@SuppressWarnings("ConstantConditions")
public class MoneyCommand extends CommandBase {
    public static final String NEED_ACCOUNT = "You do not have an account! Please run `^money create` to make one!";

    private Map<Roles, IntUnaryOperator> bonuses = new HashMap<>();
    private SQLManager sqlManager;

    public MoneyCommand(SQLManager sqlManager) {
        super("money", "<create/claim/check/bal/baltop/pay/admin>", "Command for your money needs.", CommandCategory.FUN);
        this.sqlManager = sqlManager;

        bonuses.put(Roles.HELPER, r -> r + 5);
        bonuses.put(Roles.OG, r -> r + 5);
        bonuses.put(Roles.REGULAR, r -> r + 1);
        bonuses.put(Roles.TRYHARD, r -> r + 1);
        bonuses.put(Roles.OBSESSIVE, r -> r + 1);
        bonuses.put(Roles.PATRON_BOYS, r -> r + 4);
        bonuses.put(Roles.ETHAN, r -> r + 4);
        bonuses.put(Roles.SUPPORTER, r -> r + 2);
        bonuses.put(Roles.GAMBLE2, r -> r * 2);
        bonuses.put(Roles.GAMBLE1, r -> (int) (r * 1.5));
    }

    @Override
    protected void executeCommand(CommandEvent e) {
        if (e.getArgs().isEmpty()) {
            e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
            return;
        }
        String[] args = e.getSplitArgs();
        if (args[0].equalsIgnoreCase("admin")) {
            if (!C.hasRole(e.getMember(), Roles.SUPER_ADMIN) ) {
                e.replyError(C.permMsg(Roles.SUPER_ADMIN));
                return;
            }

            if (args.length >= 2 && args[1].equalsIgnoreCase("reset-time")) {
                if (C.containsMention(e)) {
                    if (sqlManager.isActiveUserH(C.getMentionedMember(e).getUser().getId())) {
                        try {
                            sqlManager.getUser(C.getMentionedMember(e).getUser().getId()).setEpoch(0);
                            e.reply("Reset target users time!");
                            return;
                        } catch (SQLException e1) {
                            e.replyError("Oof Error.");
                        }
                    } else {
                        e.replyError("Target user does not have an account");
                        return;
                    }
                } else {
                    e.replyError(C.bold("Correct Usage:") + " ^" + name + " admin reset-time **<user>**");
                    return;
                }
            }

            if (args.length != 4) {
                e.replyError(C.bold("Correct Usage:") + " ^" + name + " admin **<give/take> <amount> <user>**");
                return;
            }
            if (!C.containsMention(e)
                || !sqlManager.isActiveUserH(C.getMentionedMember(e).getUser().getId())) {
                e.replyError(C.bold("Correct Usage:") + " ^" + name + " admin <give/take> <amount> **<user>**");
                return;
            }
            if (!StringUtils.isNumeric(args[2])) {
                e.replyError(C.bold("Correct Usage:") + " ^" + name + " admin <give/take> **<amount>** <user>");
                return;
            }

            Member target = C.getMentionedMember(e);

            if (args[1].equalsIgnoreCase("give")) {
                try {
                    UserToken token = sqlManager.getUser(target.getUser().getId());
                    token.addCoins(Integer.parseInt(args[2]));
                    e.replySuccess(C.bold("Success: ") + "Applied " + args[2] + " coins to " + C.underline(target.getEffectiveName()) + "! Their new balance is: " + C.bold(C.prettyNum(token.getCoins())));
                } catch (SQLException e1) {
                    e.replyError("Oof error.");
                }
            } else if (args[1].equalsIgnoreCase("take")) {
                try {
                    UserToken token = sqlManager.getUser(target.getUser().getId());
                    token.takeCoins(Integer.parseInt(args[2]));
                    e.replySuccess(C.bold("Success: ") + "Took " + args[2] + " coins from " + C.underline(target.getEffectiveName()) + "! Their new balance is: " + C.bold(C.prettyNum(token.getCoins())));
                } catch (SQLException e1) {
                    e.replyError("Oof error.");
                }
            } else {
                e.replyError(C.bold("Correct Usage:") +
                    " ^" + name + " admin" + C.bold("<give/take>") + "<amount> <user>");
            }
        }

        if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("bal")) {
                if (C.containsMention(e)) {
                    if (sqlManager.isActiveUserH(C.getMentionedMember(e).getUser().getId())) {
                        try {
                            e.replySuccess("User Balance is: " + C.prettyNum(sqlManager.getUser(C.getMentionedMember(e).getUser().getId()).getCoins()) + " coins!");
                            return;
                        } catch (SQLException e1) {
                            e.replyError("Oof Error!");
                            e1.printStackTrace();
                        }
                    } else {
                        e.replyError("User does not have an active money account!");
                        return;
                    }
                } else {
                    e.replyError("**Correct Usage:" + C.bold("^" + name + " bal **<user>"));
                    return;
                }
            }
        }

        if (args[0].equalsIgnoreCase("create")) {

            try {
                if (!sqlManager.isActiveUser(e.getMember().getUser().getId())) {
                    sqlManager.newUser(e.getMember().getUser().getId());
                    e.replySuccess("Your gamble account has been made!\n" +
                        "You should try " + C.code("^money claim") +  ":wink:");
                } else {
                    e.replyError("You already have an account!");
                }
            } catch (SQLException e1) {
                e.reply("Exception: " + e1.getMessage());
            }

        } else if (args[0].equalsIgnoreCase("claim")) {
            try {
                if (sqlManager.isActiveUser(e.getMember().getUser().getId())) {
                    UserToken userToken = sqlManager.getUser(e.getMember().getUser().getId());
                    int reward = payout(userToken.getUserId());
                    if (userToken.getEpoch() == 0) {
                        userToken.addCoins(reward);
                        userToken.setEpoch(System.currentTimeMillis());
                        if (reward != 200) {
                            e.replySuccess("Here is your FIRST daily money nose! +" + reward + "! \n+" + (reward - 200) + " Bonus!");
                            return;
                        }
                        e.replySuccess("Here is your FIRST daily money nose! +200");
                    } else {
                        if ((System.currentTimeMillis() - userToken.getEpoch()) >= 86400000) {
                            userToken.addCoins(reward);
                            userToken.setEpoch(System.currentTimeMillis());
                            if (reward != 200) {
                                e.replySuccess("Here is your daily money nose! +" + reward + "! \n+" + (reward - 200) + " Bonus!");
                                return;
                            }
                            e.replySuccess("Here is your daily money nose! +200");
                        } else {
                            int dif = (int) (System.currentTimeMillis() - userToken.getEpoch());
                            int wait = 24 - (((dif / 1000) / 60) / 60);
                            String unit = " hour(s)!";
                            if (wait < 1) {
                                wait = wait * 60;
                                unit = " minute(s)!";
                            }
                            e.replyError("You may only claim liquid money once a day!!1!\n"
                                + "You can reclaim your daily reward in: " + wait + unit);
                        }
                    }
                } else {
                    e.replyError(NEED_ACCOUNT);
                }
            } catch (SQLException e1) {
                e.replyError("Oof error.");
                e1.printStackTrace();
            }
        } else if (args[0].equalsIgnoreCase("bal")) {
            try {
                if (sqlManager.isActiveUser(e.getMember().getUser().getId())) {
                    e.reply("Your current balance is **" + C.prettyNum(sqlManager.getUser(e.getMember().getUser().getId()).getCoins()) + "** coins!");
                } else {
                    e.replyError(NEED_ACCOUNT);
                }
            } catch (SQLException e1) {
                e.replyError("Oof error.");
                e1.printStackTrace();
            }
        } else if (args[0].equalsIgnoreCase("baltop")) {
            try {
                Map<Integer, Map<Member, Integer>> result = sqlManager.getTop(10);
                int curPos = 1;

                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Top Balances");
                builder.setDescription("Here are the top 10 balances in the gamble system!");

                for (int i = 0; i < 10; i++) {
                    for (Map.Entry<Member, Integer> curEntry : result.get(i + 1).entrySet()) {
                        builder.addField(C.bold("#"+ curPos) + " " + curEntry.getKey().getEffectiveName(), C.bold(C.prettyNum(curEntry.getValue())) + " coins", true);
                        curPos++;
                    }
                }
                e.reply(builder.build());
            } catch (SQLException e1) {
                e.replyError("Oof error.");
            }
        } else if (args[0].equalsIgnoreCase("check")) {
            try {
                if (sqlManager.isActiveUser(e.getMember().getUser().getId())) {
                    UserToken userToken = (sqlManager.getUser(e.getMember().getUser().getId()));
                    int dif = (int) (System.currentTimeMillis() - userToken.getEpoch());
                    int wait = 24 - (((dif / 1000) / 60) / 60);
                    if (dif >= 86400000 || userToken.getEpoch() == 0) {
                        e.reply("You can reclaim your daily reward RIGHT NOW YOU DUM!");
                        return;
                    }
                    String unit = " hour(s)!";
                    if (wait < 1) {
                        wait = wait * 60;
                        unit = " minute(s)!";
                    }
                    e.reply("You can reclaim your daily reward in: " + wait + unit);
                } else {
                    e.replyError(NEED_ACCOUNT);
                }
            } catch (SQLException e1) {
                e.reply("Oof error.");
            }
        } else if (args[0].equalsIgnoreCase("pay")) {
            if (args.length != 3) {
                e.replyError(C.bold("Correct Usage:") + " ^" + name + " pay <amount> <user>");
                return;
            }
            if (!C.containsMention(e)) {
                e.replyError(C.bold("Correct Usage:") + " ^" + name + " pay <amount> **<user>**");
                return;
            }
            if (!StringUtils.isNumeric(args[1])) {
                e.replyError(C.bold("Correct Usage:") + " ^" + name + " pay **<amount>** <user>");
                return;
            }
            if (!sqlManager.isActiveUserH(e.getMember().getUser().getId()) || !sqlManager.isActiveUserH(C.getMentionedMember(e).getUser().getId())) {
                e.replyError("Both parties must have a money account please do `^money create` to get one!");
                return;
            }
            if (C.getMentionedMember(e) == e.getMember()) {
                e.replyError("You may not pay yourself! I know wut ur trying to do...");
                return;
            }

            try {
                UserToken fromToken = sqlManager.getUser(e.getMember().getUser().getId());
                UserToken toToken = sqlManager.getUser(C.getMentionedMember(e).getUser().getId());
                int amount = Integer.parseInt(args[1]);
                if (fromToken.getCoins() < amount) {
                    e.replyError("You do not have valid funds to complete this transaction.");
                    return;
                }
                fromToken.takeCoins(amount);
                toToken.addCoins(amount);
                e.replySuccess("Successfully paid " + C.bold(C.getMentionedMember(e).getEffectiveName()) + " " + C.underline(C.prettyNum(amount) + " coins!"));
            } catch (SQLException e1) {
                e.replyError("Oof Error");
            }
        } else {
            e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
        }

    }

    private int payout(String userId) {
        int reward = 200;

        Member member = C.getGuild().getMemberById(userId);
        
        for(Map.Entry<Roles, IntUnaryOperator> bonus : bonuses.entrySet()) {
            if (C.hasRole(member, bonus.getKey())) {
                reward = bonus.getValue().applyAsInt(reward);
            }
        }

        return reward;
    }

}
