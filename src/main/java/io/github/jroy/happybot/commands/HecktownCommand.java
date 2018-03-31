package io.github.jroy.happybot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.util.concurrent.ThreadLocalRandom;

public class HecktownCommand extends Command {
    private String[] hecktownLocations = new String[] { "https://goo.gl/Cik7vC", "https://goo.gl/cenQgf",
            "https://goo.gl/eeukMu", "https://goo.gl/YGF5eX", "https://goo.gl/LjaEyC"};

    public HecktownCommand() {
        this.name = "hecktown";
        this.help = "Welcome to Hecktown!";
        this.category = new Category("Fun");
    }

    @Override
    protected void execute(CommandEvent event) {
        event.replySuccess(hecktownLocations[ThreadLocalRandom.current().nextInt(hecktownLocations.length)]);
    }
}
