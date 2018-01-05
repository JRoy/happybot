package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.util.MessageFactory;

public class WelcomeStatsCommand extends Command {

    private MessageFactory messageFactory;

    public WelcomeStatsCommand(MessageFactory messageFactory) {
        this.name = "welcomestats";
        this.help = "Gives the stats of the Welcome Messages.";
        this.guildOnly = false;
        this.category = new Category("Fun");
        this.messageFactory = messageFactory;
    }

    @Override
    protected void execute(CommandEvent e) {
        e.getChannel().sendMessage(":information_source: Welcome Queue Stats:\n**Welcome Messages:** " + messageFactory.getTotals(MessageFactory.MessageType.JOIN) + "\n**Quit Messages:** " + messageFactory.getTotals(MessageFactory.MessageType.LEAVE)).queue();
    }
}
