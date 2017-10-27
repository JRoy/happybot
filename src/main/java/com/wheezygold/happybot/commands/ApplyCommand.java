package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;

public class ApplyCommand extends Command {
    public ApplyCommand() {
        this.name = "apply";
        this.help = "Gives the link to the application.";
        this.category = new Category("General");
    }

    @Override
    protected void execute(CommandEvent e) {
        e.replySuccess("Here you go!\nhttps://goo.gl/forms/vB6lfA8VhIMqxFDs2");
    }
}
