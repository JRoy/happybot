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
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

public class GambleCommand extends Command {

    private SQLManager sqlManager;
    private HashMap<Member, OffsetDateTime> gambleTimes = new HashMap<>();

    public GambleCommand(SQLManager sqlManager) {
        this.name = "gamble";
        this.help = "Gambling Command, please type `^gamble help` for details.";
        this.arguments = "help";
        this.guildOnly = true;
        this.category = new Category("Fun");
        this.sqlManager = sqlManager;
    }

    @Override
    protected void execute(CommandEvent e) {
        if (e.getArgs().isEmpty() || e.getArgs().equalsIgnoreCase("help")) {
            e.reply("**Gamble System Overview:**\n" +
                    "You may only gamble away your life savings every 3 minutes to avoid life destruction.\n" +
                    "Patron Boys and Mods+ may gamble their collage savings every minute.\n" +
                    "To start a gamble you can do `^gamble <amount of coins ranging from 100-10000>`\n" +
                    "You have a 50% chance of you getting your bet  and a 50% chance of your bet being lost.\n");
            return;
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

            if (gambleTimes.containsKey(e.getMember())) {
                int time = (int) OffsetDateTime.now().until(gambleTimes.get(e.getMember()), ChronoUnit.SECONDS);
                if (time>0) {
                    e.replyError("You must wait " + String.valueOf(time) + " seconds before preforming this again!" );
                    return;
                }
            }

            UserToken userToken = sqlManager.getUser(e.getMember().getUser().getId());
            int bet = Integer.parseInt(e.getArgs());

            if (bet > 10000 || bet < 100) {
                e.replyError("Your placed bet is out of the bet range, please do `^gamble help` to learn more!");
                return;
            }

            if (bet > userToken.getCoins()) {
                e.replyError("You do not have sufficient funds to complete this gamble!");
                return;
            }

            if (C.hasRole(e.getMember(), Roles.PATRON_BOYS) || C.hasRole(e.getMember(), Roles.MODERATOR)) {
                gambleTimes.put(e.getMember(), OffsetDateTime.now().plusSeconds(60));
            } else {
                gambleTimes.put(e.getMember(), OffsetDateTime.now().plusSeconds(180));
            }

            double p = Math.random();

            if (p < 0.5) {
                userToken.addCoins(bet);
                e.reply("YOU BET ON YEEZY WELL! +" + String.valueOf(bet) + " coins!");
            } else {
                userToken.takeCoins(bet);
                e.reply("Should have aimed smaller... -" + String.valueOf(bet) + " coins.");
            }
        } catch (SQLException e1) {
            e.replyError("Error while executing: " + e1.getMessage());
        }
    }
}
