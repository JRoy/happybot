package io.github.jroy.happybot.commands.money;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.sql.SQLManager;
import io.github.jroy.happybot.sql.UserToken;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;
import io.github.jroy.happybot.util.RuntimeEditor;
import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

public class GambleCommand extends CommandBase {
  private final SQLManager sqlManager;
  private final HashMap<Member, OffsetDateTime> gambleTimes = new HashMap<>();

  public GambleCommand(SQLManager sqlManager) {
    super("gamble", "<help/check>", "Gambling Command, please type `^gamble help` for details.", CommandCategory.FUN);
    this.sqlManager = sqlManager;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (e.getArgs().isEmpty() || e.getArgs().equalsIgnoreCase("help")) {
      e.reply("**Gamble System Overview:**\n" +
          "You may only gamble away your life savings every 3 minutes to avoid life destruction.\n" +
          "Patron Boys and Mods+ may gamble their college savings every minute.\n" +
          "To start a gamble you can do " + C.escape("^gamble <amount of coins ranging from 100-" + RuntimeEditor.getGambleMax() + ">") + "\n" +
          (RuntimeEditor.getGambleJackpot() > 0 ? "You also have a chance at winning the :moneybag: jackpot by doing ^gamble all (limited time only)\n" : "") +
          "You have a 50% chance of you getting your bet and a 50% chance of your bet being lost.\n");
      return;
    }

    if (e.getArgs().equalsIgnoreCase("check") && isGambleMember(e.getMember())) {
      int timeRemaining = getTimeRemaining(e.getMember());
      if (timeRemaining > 0) {
        e.reply("You have " + timeRemaining + " seconds before using the gamble command!");
      } else {
        e.reply("You can use the gamble command now!");
      }
    }

    boolean gambleAll = e.getArgs().equalsIgnoreCase("all") && RuntimeEditor.getGambleJackpot() > 0;
    if (!StringUtils.isNumeric(e.getArgs()) && !gambleAll) {
      e.replyError("Please do `^gamble help` for the correct usage!");
      return;
    }
    try {
      if (!sqlManager.isActiveUser(e.getMember().getUser().getId())) {
        e.replyError(MoneyCommand.NEED_ACCOUNT);
        return;
      }

      if (isGambleMember(e.getMember())) {
        int time = getTimeRemaining(e.getMember());
        if (time > 0) {
          e.replyError("You must wait " + time + " seconds before preforming this again!");
          return;
        }
      }

      UserToken userToken = sqlManager.getUser(e.getMember().getUser().getId());

      int bet = gambleAll ? userToken.getCoins() : Integer.parseInt(e.getArgs());
      if (!gambleAll && (bet > RuntimeEditor.getGambleMax() || bet < 100)) {
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

      double random = Math.random();
      if (gambleAll && random < RuntimeEditor.getGambleJackpot()) {
        int jackpot = bet * 9;
        userToken.addCoins(jackpot);
        e.reply(":moneybag: :moneybag: YOU WON THE JACKPOT! +" + jackpot + " coins! :moneybag: :moneybag:");
      } else if (random < 0.5) {
        userToken.addCoins(bet);
        e.reply(e.getMember().getAsMention() + " YOU BET ON YEEZY WELL! +" + bet + " coins!" + " You now have a balance of " + C.bold(C.prettyNum(userToken.getCoins()) + " coins!"));
      } else {
        userToken.takeCoins(bet);
        e.reply(e.getMember().getAsMention() + " Should have aimed smaller... -" + bet + " coins." + " You now have a balance of " + C.bold(C.prettyNum(userToken.getCoins()) + " coins!"));
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
