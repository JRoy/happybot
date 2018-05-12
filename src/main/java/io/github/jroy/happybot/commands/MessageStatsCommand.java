package io.github.jroy.happybot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.github.jroy.happybot.sql.MessageFactory;

public class MessageStatsCommand extends Command {

    private MessageFactory messageFactory;

    public MessageStatsCommand(MessageFactory messageFactory) {
        this.name = "messagestats";
        this.aliases = new String[]{"msgstats", "mstats"};
        this.help = "Gives the stats of the Random Messages.";
        this.guildOnly = false;
        this.category = new Category("Fun");
        this.messageFactory = messageFactory;
    }

    @Override
    protected void execute(CommandEvent e) {
        e.getChannel().sendMessage(":information_source: Message Queue Stats:" +
                "\n**Welcome Messages:** " + messageFactory.getTotals(MessageFactory.MessageType.JOIN) +
                "\n**Quit Messages:** " + messageFactory.getTotals(MessageFactory.MessageType.LEAVE) +
                "\n**Warning Messages:** " + messageFactory.getTotals(MessageFactory.MessageType.WARN) +
                "\n**Update Start Messages:** " + messageFactory.getTotals(MessageFactory.MessageType.UPDATE_START)+
                "\n**Update End Messages:** " + messageFactory.getTotals(MessageFactory.MessageType.UPDATE_END)).queue();
    }
}
