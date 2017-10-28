package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;

import java.time.temporal.ChronoUnit;

/**
 * @author John Grosh (jagrosh)
 */

public class PingCommand extends Command {

    public PingCommand() {
        this.name = "ping";
        this.help = "checks the bot's latency";
        this.guildOnly = false;
        this.aliases = new String[]{"pong"};
        this.category = new Category("General");
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply("Ping: ...", m -> m.editMessage("Ping: " + event.getMessage().getCreationTime().until(m.getCreationTime(), ChronoUnit.MILLIS) + "ms | Websocket: " + event.getJDA().getPing() + "ms").queue());
    }

}
