package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.events.WelcomeMessage;

public class WelcomeStatsCommand extends Command {
    public WelcomeStatsCommand() {
        this.name = "welcomestats";
        this.help = "Gives the stats of the Welcome Messages.";
        this.guildOnly = false;
        this.category = new Category("Fun");
    }

    @Override
    protected void execute(CommandEvent e) {
        WelcomeMessage.showStats(e.getChannel().getId());
    }
}
