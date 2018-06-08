package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;

import java.time.temporal.ChronoUnit;

/**
 * Converted to CommandBase by @JRoy
 * @author John Grosh (jagrosh)
 */

public class PingCommand extends CommandBase {

    public PingCommand() {
        super("ping", null, "Checks the **BOT**'s latency to Discord", CommandCategory.GENERAL);
        this.aliases = new String[]{"pong"};
    }

    @Override
    protected void executeCommand(CommandEvent event) {
        event.reply("Ping: ...", m -> m.editMessage("Ping: " + event.getMessage().getCreationTime().until(m.getCreationTime(), ChronoUnit.MILLIS) + "ms | Websocket: " + event.getJDA().getPing() + "ms").queue());
    }

}
