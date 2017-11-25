package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;

public class SeasonCommand extends Command {
    public SeasonCommand() {
        this.name = "seasons";
        this.help = "Lists happyheart's seasons.";
        this.guildOnly = false;
        this.category = new Category("General");
    }

    @Override
    protected void execute(CommandEvent e) {
        e.replySuccess("**Happyheart Seasons:**\n" +
                "\n" +
                "Season 1 - <http://bit.ly/2y0akBf>\n" +
                "Season 2 - <http://bit.ly/2kxqexc>\n" +
                "Season 3 - <http://bit.ly/2xpJ3Zm>\n" +
                "Season 4 - <http://bit.ly/2fTJKPD>\n" +
                "Season 5 - <http://bit.ly/2xq8dCu>");
    }
}
