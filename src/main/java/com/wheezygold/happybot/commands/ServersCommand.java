package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;

public class ServersCommand extends Command {
    public ServersCommand() {
        this.name = "servers";
        this.help = "Gives a list of servers happyheart has played on!";
        this.guildOnly = false;
        this.category = new Category("General");
    }

    @Override
    protected void execute(CommandEvent e) {
        e.replySuccess("**List of Servers Happyheart Plays On:**\n" +
                "\n" +
                "Hypixel - play.hypixel.net\n" +
                "Mineplex - us.mineplex.com\n" +
                "Jumpcraft - play.jumpcraft.org\n" +
                "Weebcraft - weebcraftpk.myserver.gs\n" +
                "Tidecraft - tidecraft.beastmc.com)");
    }
}
