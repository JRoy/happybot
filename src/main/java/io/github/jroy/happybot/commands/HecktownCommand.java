package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;

import java.util.concurrent.ThreadLocalRandom;

public class HecktownCommand extends CommandBase {
    private String[] hecktownLocations = new String[] { "https://goo.gl/Cik7vC", "https://goo.gl/cenQgf",
            "https://goo.gl/eeukMu", "https://goo.gl/YGF5eX", "https://goo.gl/LjaEyC"};

    public HecktownCommand() {
        super("hecktown", null, "Welcome to Hecktown!", CommandCategory.FUN);
    }

    @Override
    protected void executeCommand(CommandEvent event) {
        event.replySuccess(hecktownLocations[ThreadLocalRandom.current().nextInt(hecktownLocations.length)]);
    }
}
