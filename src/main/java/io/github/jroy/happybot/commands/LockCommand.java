package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.PermissionOverride;
import net.dv8tion.jda.core.managers.PermOverrideManager;

public class LockCommand extends CommandBase {

    public LockCommand() {
        super("lock", null, "Locks the current channel!", CommandCategory.STAFF, Roles.SUPER_ADMIN);
    }

    @Override
    protected void executeCommand(CommandEvent e) {
        try {
            PermissionOverride permissionOverride = e.getTextChannel().getPermissionOverride(Roles.EVERYONE.getRole());
            PermOverrideManager manager = permissionOverride.getManager();
            manager.deny(Permission.MESSAGE_WRITE).queue();
            e.replySuccess(":lock: Channel has been locked!");
        } catch (NullPointerException npe) {
            e.replyError("An error occurred while locking the channel! Please make sure this channel is setup correctly.");
    }
    }
}
