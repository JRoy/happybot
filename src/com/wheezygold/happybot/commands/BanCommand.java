package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;

public class BanCommand extends Command {
    public BanCommand() {
        this.name = "staffmng";
        this.help = "A command to help Recruiters with their job.";
        this.arguments = "<deny/deny-level/approve> <user>";
        this.guildOnly = true;
        this.category = new Category("Staff Tools");
    }

    @Override
    protected void execute(CommandEvent commandEvent) {

    }
}
