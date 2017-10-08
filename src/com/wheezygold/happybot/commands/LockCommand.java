package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.util.Roles;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.PermissionOverride;
import net.dv8tion.jda.core.managers.PermOverrideManager;

public class LockCommand extends Command {
    public LockCommand() {
        this.name = "lock";
        this.help = "Locks the current channel!";
        this.guildOnly = true;
        this.category = new Category("Staff Tools");
    }

    @Override
    protected void execute(CommandEvent e) {
        PermissionOverride permissionOverride = e.getTextChannel().getPermissionOverride(Roles.EVERYONE.getrole(e.getGuild()));

        PermOverrideManager manager = permissionOverride.getManager();

        manager.deny(Permission.MESSAGE_WRITE).queue();

        e.replySuccess(":lock: Channel has been locked!");
    }
}
