package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.Roles;

public class RewardsCommand extends CommandBase {

    public RewardsCommand() {
        super("rewards", null, "Displays the role rewards for MEE6 XP.", CommandCategory.GENERAL);
    }

    @Override
    protected void executeCommand(CommandEvent e) {
        e.reply("**Level Rewards:**\n" +
                "Level 10 - **" + Roles.REGULAR.getRole().getName() + "**\n" +
                "Level 20 - **" + Roles.TRYHARD.getRole().getName() + "**\n" +
                "Level 30 - **" + Roles.OBSESSIVE.getRole().getName() + "**\n" +
                "Level 50 - **" + Roles.OG.getRole().getName() + "**\n"
        );
    }
}
