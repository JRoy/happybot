package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.util.C;

public class ShutdownCommand extends Command {

    public ShutdownCommand()
    {
        this.name = "shutdown";
        this.help = "Shutdowns the bot with love";
        this.guildOnly = false;
        this.ownerCommand = true;
        this.category = new Category("Bot Management");
    }

    @Override
    protected void execute(CommandEvent e) {
        e.getMessage().addReaction("✅").queue();
        e.getJDA().shutdown();
        C.log("The JDA instance has been shutdown...exiting the program.");
        System.exit(0);
    }

}