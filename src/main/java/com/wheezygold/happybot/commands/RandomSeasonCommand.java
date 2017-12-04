package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;

import java.util.Random;

public class RandomSeasonCommand extends Command {

    private Random random;
    private String[] seasons;

    public RandomSeasonCommand() {
        this.name = "randomseason";
        this.help = "Gives you a random happyheart season!";
        this.guildOnly = false;
        this.category = new Category("Fun");

        seasons = new String[]{
                "Season 1 - <http://bit.ly/2y0akBf>",
                "Season 2 - <http://bit.ly/2kxqexc>",
                "Season 3 - <http://bit.ly/2xpJ3Zm>",
                "Season 4 - <http://bit.ly/2fTJKPD>",
                "Season 5 - <http://bit.ly/2xq8dCu>"
        };
        random = new Random();
    }

    @Override
    protected void execute(CommandEvent e) {
        e.reply(":game_die: Rolling the dice! :game_die:\n" + seasons[random.nextInt(seasons.length)]);
    }
}
