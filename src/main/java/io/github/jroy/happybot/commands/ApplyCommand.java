package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;

public class ApplyCommand extends CommandBase {

    public ApplyCommand() {
        super("apply", null, "Gives you the link to apply for staff!", CommandCategory.GENERAL);
    }

    @Override
    protected void executeCommand(CommandEvent e) {
        e.replySuccess("Here you go!\nhttps://goo.gl/forms/vB6lfA8VhIMqxFDs2");
    }
}
