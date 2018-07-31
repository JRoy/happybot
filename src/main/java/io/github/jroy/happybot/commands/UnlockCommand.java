package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.PermissionOverride;
import net.dv8tion.jda.core.managers.PermOverrideManager;

public class UnlockCommand extends CommandBase {

  public UnlockCommand() {
    super("unlock", null, "Unlocks the current channel!", CommandCategory.STAFF, Roles.SUPER_ADMIN);
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    try {
      PermissionOverride permissionOverride = e.getTextChannel().getPermissionOverride(Roles.EVERYONE.getRole());
      PermOverrideManager manager = permissionOverride.getManager();
      manager.grant(Permission.MESSAGE_WRITE).queue();
      e.replySuccess(":unlock: Channel has been unlocked!");
    } catch (NullPointerException npe) {
      e.replyError("An error occurred while un-locking the channel! Please make sure this channel is setup correctly.");
    }
  }
}
