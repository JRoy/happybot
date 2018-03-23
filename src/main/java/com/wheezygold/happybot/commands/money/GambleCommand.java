package com.wheezygold.happybot.commands.money;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.wheezygold.happybot.sql.SQLManager;
import com.wheezygold.happybot.sql.UserToken;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Roles;
import com.wheezygold.happybot.util.RuntimeEditor;
import net.dv8tion.jda.core.entities.Member;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

public class GambleCommand extends Command {

    private SQLManager sqlManager;
    private HashMap<Member, OffsetDateTime> gambleTimes = new HashMap<>();

    public GambleCommand(SQLManager sqlManager) {
        this.name = "gamble";
        this.help = "Gambling Command, please type `^gamble help` for details.";
        this.arguments = "<help/check>";
        this.guildOnly = true;
        this.category = new Category("Fun");
        this.sqlManager = sqlManager;
    }

    @Override
    protected void execute(CommandEvent e) {
        if (e.getArgs().isEmpty() || e.getArgs().equalsIgnoreCase("help")) {
            e.reply("**Gamble System Overview:**\n" +
                    "You may only gamble away your life savings every 3 minutes to avoid life destruction.\n" +
                    "Patron Boys and Mods+ may gamble their college savings every minute.\n" +
                    "To start a gamble you can do `^gamble <amount of coins ranging from 100-"+ String.valueOf(RuntimeEditor.getGambleMax()) + ">`\n" +
                    "You have a 50% chance of you getting your bet  and a 50% chance of your bet being lost.\n");
            return;
        }

        if (e.getArgs().equalsIgnoreCase("check") && isGambleMember(e.getMember())) {
            int timeRemaining = getTimeRemaining(e.getMember());
            if (timeRemaining > 0) {
                e.reply("You have " + String.valueOf(timeRemaining) + " seconds before using the gamble command!");
            } else {
                e.reply("You can use the gamble command now!");
            }
        }

        if (!StringUtils.isNumeric(e.getArgs())) {
            e.replyError("Please do `^gamble help` for the correct usage!");
            return;
        }
        try {
            if (!sqlManager.isActiveUser(e.getMember().getUser().getId())) {
                e.replyError("You do not have an account! Please run `^money create` to make one!");
                return;
            }

            if (isGambleMember(e.getMember())) {
            	int time = getTimeRemaining(e.getMember());
                if (time > 0) {
                    e.replyError("You must wait " + String.valueOf(time) + " seconds before preforming this again!" );
                    return;
                }
            }

            UserToken userToken = sqlManager.getUser(e.getMember().getUser().getId());
            int bet = Integer.parseInt(e.getArgs());

            if (bet > RuntimeEditor.getGambleMax() || bet < 100) {
                e.replyError("Your placed bet is out of the bet range, please do `^gamble help` to learn more!");
                return;
            }

            if (bet > userToken.getCoins()) {
                e.replyError("You do not have sufficient funds to complete this gamble!");
                return;
            }

            if (C.hasRole(e.getMember(), Roles.PATRON_BOYS) || C.hasRole(e.getMember(), Roles.MODERATOR) || C.hasRole(e.getMember(), Roles.ETHAN)) {
                gambleTimes.put(e.getMember(), OffsetDateTime.now().plusSeconds(30));
            } else {
                gambleTimes.put(e.getMember(), OffsetDateTime.now().plusSeconds(60));
            }

            if (Math.random() < 0.5) {
                userToken.addCoins(bet);
                e.reply(e.getMember().getAsMention() + " YOU BET ON YEEZY WELL! +" + String.valueOf(bet) + " coins!" + " You now have a balance of " + C.bold(C.prettyNum(userToken.getCoins()) + " coins!"));
            } else {
                userToken.takeCoins(bet);
                e.reply(e.getMember().getAsMention() + " Should have aimed smaller... -" + String.valueOf(bet) + " coins." + " You now have a balance of " + C.bold(C.prettyNum(userToken.getCoins()) + " coins!"));
            }
        } catch (SQLException e1) {
            e.replyError("Error while executing: " + e1.getMessage());
        }
    }

    private boolean isGambleMember(Member member) {
        return gambleTimes.containsKey(member);
    }

    private int getTimeRemaining(Member member) {
        return (int) OffsetDateTime.now().until(gambleTimes.get(member), ChronoUnit.SECONDS);
    }
}
