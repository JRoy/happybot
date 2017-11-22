package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.sql.SQLManager;

public class GambleCommand extends Command {

    private SQLManager sqlManager;

    public GambleCommand(SQLManager sqlManager) {
        this.name = "gamble";
        this.help = "Command for all the gambling system.";
        this.arguments = "<bet amount> <number guess>";
        this.guildOnly = true;
        this.category = new Category("Fun");
        this.sqlManager = sqlManager;
    }

    @Override
    protected void execute(CommandEvent e) {
        if (e.getArgs().isEmpty()) {
            e.reply("**Gamble System Overview:**" +
                    "Use ^money");
        }
    }
}
