package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.util.Roles;

public class RewardsCommand extends Command {

    public RewardsCommand() {
        this.name = "rewards";
        this.help = "Gives the role rewards.";
        this.guildOnly = false;
        this.category = new Category("General");
    }

    @Override
    protected void execute(CommandEvent e) {
        e.reply("**Level Rewards:**\n" +
                "Level 10 - **" + Roles.REGULAR.getrole().getName() + "**\n" +
                "Level 20 - **" + Roles.TRYHARD.getrole().getName() + "**\n" +
                "Level 30 - **" + Roles.OBSESSIVE.getrole().getName() + "**\n" +
                "Level 50 - **" + Roles.OG.getrole().getName() + "**\n"
        );
    }
}
