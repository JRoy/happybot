package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.sql.SQLManager;

@SuppressWarnings("FieldCanBeLocal")
public class ShopCommand extends Command {

    private SQLManager sqlManager;

    public ShopCommand(SQLManager sqlManager) {
        this.name = "shop";
        this.help = "Fun activity thing let's you guy things.";
        this.arguments = "<page/buy/help>";
        this.guildOnly = true;
        this.category = new Category("Fun");
        this.sqlManager = sqlManager;
    }

    private String currentShopHelp = "**Happyheart Shop Help**\n" +
            "This shop allows for you to spend your money on\n" +
            "stuff. To view the products we offer please do\n" +
            "`^shop page <number>` to get the list of the shop items.";
    private String currentShop = "";

    @Override
    protected void execute(CommandEvent e) {
        if (e.getArgs().isEmpty()) {
            e.replyError(currentShopHelp);
        }
    }
}
