package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.C;
import net.dv8tion.jda.core.entities.Member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ShippingCommand extends CommandBase {

    public ShippingCommand() {
        super("ship", "<user> <user>", "Will check the shipability of two users", CommandCategory.FUN);
        this.aliases = new String[] {"shipping", "match"};
    }

    @Override
    protected void executeCommand(CommandEvent e) {

        if (e.getArgs().isEmpty()) {
            e.reply("Shipping Ratings:\n" +
                    ":yellow_heart: = 100%\n" +
                    ":two_hearts: = 50%+\n" +
                    ":heart: 30%+\n" +
                    ":black_heart: 20+\n" +
                    ":broken_heart: 0%+"); //No return statement is intentional here...
        }

        if (e.getMentionsAmount() != 2) {
            e.replyError(invalid);
            return;
        }

        Member firstUser = e.getMentionedMember(0);
        Member secondUser = e.getMentionedMember(1);
        String firstName = firstUser.getUser().getName();
        String secondName = secondUser.getUser().getName();

        String shipName = firstName.substring(0, firstName.length() / 2) + secondName.substring(secondName.length() / 2);

        long seed = firstUser.getUser().getIdLong() ^ secondUser.getUser().getIdLong();
        int match = new Random(Long.hashCode(seed)).nextInt(101);

        String emote;
        if (match == 100) {
            emote = ":yellow_heart:";
        } else if (match >= 50) {
            emote = ":two_hearts:";
        } else if (match >= 30) {
            emote = ":heart:";
        } else if (match >= 20) {
            emote = ":black_heart:";
        } else {
            emote = ":broken_heart:";
        }

        e.reply(":heart_exclamation:" + C.bold("Shipping") + ":heart_exclamation:\n" +
                firstUser.getAsMention() + " (" + emote + ") " + secondUser.getAsMention() + "\n" +
                shipName + " is a " + match + "% match!");
    }

}
