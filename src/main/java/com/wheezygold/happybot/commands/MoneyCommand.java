package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.sql.SQLManager;
import com.wheezygold.happybot.sql.UserToken;
import com.wheezygold.happybot.util.C;
import net.dv8tion.jda.core.entities.Member;

import java.sql.SQLException;
import java.util.Map;

public class MoneyCommand extends Command {

    private SQLManager sqlManager;

    public MoneyCommand(SQLManager sqlManager) {
        this.name = "money";
        this.help = "Command for your money needs.";
        this.arguments = "<create/claim/check/bal/baltop>";
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
        if (args[0].equalsIgnoreCase("create")) {

            try {
                if (!sqlManager.isActiveUser(e.getMember().getUser().getId())) {
                    sqlManager.newUser(e.getMember().getUser().getId());
                    e.replySuccess("Your gamble account has been made!");
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
                    if (userToken.getEpoch() == 0) {
                        userToken.setCoins(userToken.getCoins() + 200);
                        userToken.setEpoch(System.currentTimeMillis());
                        e.replySuccess("Here is your FIRST daily money nose! +200");
                    } else {
                        if ((System.currentTimeMillis() - userToken.getEpoch()) >= 86400000) {
                            userToken.setCoins(userToken.getCoins() + 200);
                            userToken.setEpoch(System.currentTimeMillis());
                            e.replySuccess("Here is your daily money nose! +200");
                        } else {
                            e.replyError("You may only claim liquid money once a day!!1!");
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
                        topBal.append("\n" + C.bold("- #" + String.valueOf(curPos)) + " " + C.underline(curEntry.getKey().getEffectiveName()) + C.slant(" with " + C.bold(C.prettyNum(curEntry.getValue()) + " coins")));
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
                    int dif = new Long(System.currentTimeMillis()).intValue() - new Long(sqlManager.getUser(e.getMember().getUser().getId()).getEpoch()).intValue();
                    int fin = (dif / 1000) / 60;
                    String unit = " minutes!";
                    if (fin > 60) {
                        fin = fin / 60;
                        unit = " hours!";
                    }
                    e.reply("You can reclaim your daily reward in: " + fin + unit);
                } else {
                    e.replyError(needAccount());
                }
            } catch (SQLException e1) {
                e.reply("Oof error.");
            }
        } else {
            e.replyError("**Correct Usage:** ^" + name + " " + arguments);
        }

    }

    private String needAccount() {
        return "You do not have an account! Please run `^money create` to make one!";
    }

}
