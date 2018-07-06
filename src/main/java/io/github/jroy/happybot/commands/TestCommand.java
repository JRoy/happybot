package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.Roles;

public class TestCommand extends CommandBase {

    public TestCommand() {
        super("test", null, "The world may never know!", CommandCategory.BOT, Roles.DEVELOPER);
    }

    @Override
    protected void executeCommand(CommandEvent e) {
        e.reply("lol no");
    }
}
