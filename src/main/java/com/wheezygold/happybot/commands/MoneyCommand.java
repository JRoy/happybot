package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.sql.SQLManager;
import com.wheezygold.happybot.sql.UserToken;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Roles;
import net.dv8tion.jda.core.entities.Member;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
public class MoneyCommand extends Command {

    private SQLManager sqlManager;

    public MoneyCommand(SQLManager sqlManager) {
        this.name = "money";
        this.help = "Command for your money needs.";
        this.arguments = "<create/claim/check/bal/baltop/pay/admin>";
        this.guildOnly = true;
        this.category = new Category("Fun");
        this.sqlManager = sqlManager;
    }

    @Override
    protected void execute(CommandEvent e) {
        if (e.getArgs().isEmpty()) {
            e.replyError("**Correct Usage:** ^" + name + " " + arguments);
            return;
        }
        String[] args = e.getArgs().split("[ ]");
        if (args[0].equalsIgnoreCase("admin")) {
            if (!C.hasRole(e.getMember(), Roles.ADMIN) ) {
                e.replyError(C.permMsg(Roles.ADMIN));
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
                    e.replyError("**Correct Usage:** ^" + name + " admin reset-time **<user>**");
                    return;
                }
            }

            if (args.length != 4) {
                e.replyError("**Correct Usage:** ^" + name + " admin **<give/take> <amount> <user>**");
                return;
            }
            if (!C.containsMention(e)) {
                e.replyError("**Correct Usage:** ^" + name + " admin <give/take> <amount> **<user>**");
                return;
            }

            if (!sqlManager.isActiveUserH(C.getMentionedMember(e).getUser().getId())) {
                e.replyError("**Correct Usage:** ^" + name + " admin <give/take> <amount> **<user>**");
                return;
            }

            if (!StringUtils.isNumeric(args[2])) {
                e.replyError("**Correct Usage:** ^" + name + " admin <give/take> **<amount>** <user>");
                return;
            }

            if (args[1].equalsIgnoreCase("give")) {
                try {
                    Member target = C.getMentionedMember(e);
                    UserToken token = sqlManager.getUser(target.getUser().getId());
                    token.addCoins(Integer.parseInt(args[2]));
                    e.replySuccess(C.bold("Success: ") + "Applied " + args[2] + " coins to " + C.underline(target.getEffectiveName()) + "! Their new balance is: " + C.bold(C.prettyNum(token.getCoins())));
                } catch (SQLException e1) {
                    e.replyError("Oof error.");
                }
            } else if (args[1].equalsIgnoreCase("take")) {
                try {
                    Member target = C.getMentionedMember(e);
                    UserToken token = sqlManager.getUser(target.getUser().getId());
                    token.takeCoins(Integer.parseInt(args[2]));
                    e.replySuccess(C.bold("Success: ") + "Took " + args[2] + " coins from " + C.underline(target.getEffectiveName()) + "! Their new balance is: " + C.bold(C.prettyNum(token.getCoins())));
                } catch (SQLException e1) {
                    e.replyError("Oof error.");
                }
            } else {
                e.replyError("**Correct Usage:** ^" + name + " admin **<give/take>** <amount> <user>");
            }
        } else if (args[0].equalsIgnoreCase("create")) {

            try {
                if (!sqlManager.isActiveUser(e.getMember().getUser().getId())) {
                    sqlManager.newUser(e.getMember().getUser().getId());
                    e.replySuccess("Your gamble account has been made!\nYou should try `^money claim` :wink:");
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
                            e.replySuccess("Here is your FIRST daily money nose! +" + String.valueOf(reward) + "! \n+" + String.valueOf(reward - 200) + " Bonus!");
                            return;
                        }
                        e.replySuccess("Here is your FIRST daily money nose! +200");
                    } else {
                        if ((System.currentTimeMillis() - userToken.getEpoch()) >= 86400000) {
                            userToken.addCoins(reward);
                            userToken.setEpoch(System.currentTimeMillis());
                            if (reward != 200) {
                                e.replySuccess("Here is your daily money nose! +" + String.valueOf(reward) + "! \n+" + String.valueOf(reward - 200) + " Bonus!");
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
                            e.replyError("You may only claim liquid money once a day!!1!\n" + "You can reclaim your daily reward in: " + wait + unit);
                        }
                    }
                } else {
                    e.replyError(needAccount());
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
                    e.replyError(needAccount());
                }
            } catch (SQLException e1) {
                e.replyError("Oof error.");
                e1.printStackTrace();
            }
        } else if (args[0].equalsIgnoreCase("baltop")) {
            try {
                Map<Integer, Map<Member, Integer>> result = sqlManager.getTop(10);
                int curPos = 1;
                StringBuilder topBal = new StringBuilder();
                topBal.append(C.bold("Top Balances:"));

                for (int i = 0; i < 10; i++) {
                    for (Map.Entry<Member, Integer> curEntry : result.get(i + 1).entrySet()) {
                        topBal.append("\n")
                                .append(C.bold("- #" + String.valueOf(curPos)))
                                .append(" ")
                                .append(C.underline(curEntry.getKey().getEffectiveName()))
                                .append(C.slant(" with "))
                                .append(C.bold(C.prettyNum(curEntry.getValue()) + " coins"));
                        curPos++;
                    }
                }
                e.reply(topBal.toString());
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
                    e.replyError(needAccount());
                }
            } catch (SQLException e1) {
                e.reply("Oof error.");
            }
        } else if (args[0].equalsIgnoreCase("pay")) {
            if (args.length != 3) {
                e.replyError("**Correct Usage:** ^" + name + " pay <amount> <user>");
                return;
            }
            if (!C.containsMention(e)) {
                e.replyError("**Correct Usage:** ^" + name + " pay <amount> **<user>**");
                return;
            }
            if (!StringUtils.isNumeric(args[1])) {
                e.replyError("**Correct Usage:** ^" + name + " pay **<amount>** <user>");
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
            e.replyError("**Correct Usage:** ^" + name + " " + arguments);
        }

    }

    private String needAccount() {
        return "You do not have an account! Please run `^money create` to make one!";
    }

    private int payout(String userId) {
        int reward = 200;
        Member member = C.getGuild().getMemberById(userId);
        if (C.hasRole(member, Roles.HELPER)) {
            reward = reward + 5;
        }
        if (C.hasRole(member, Roles.OG)) {
            reward = reward + 5;
        }
        if (C.hasRole(member, Roles.REGULAR)) {
            reward++;
        }
        if (C.hasRole(member, Roles.TRYHARD)) {
            reward++;
        }
        if (C.hasRole(member, Roles.OBSESSIVE)) {
            reward++;
        }
        if (C.hasRole(member, Roles.PATRON_BOYS) || C.hasRole(member, Roles.ETHAN)) {
            reward = reward + 4;
        }
        if (C.hasRole(member, Roles.SUPPORTER)) {
            reward = reward + 2;
        }

        if (C.hasRole(member, Roles.GAMBLE2))
            return reward * 2;

        if (C.hasRole(member, Roles.GAMBLE1))
            return (int) (reward * 1.5);

        return reward;
    }

}
