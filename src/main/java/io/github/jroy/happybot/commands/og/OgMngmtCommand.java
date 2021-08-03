package io.github.jroy.happybot.commands.og;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.Roles;

public class OgMngmtCommand extends CommandBase {
  public OgMngmtCommand() {
    super("userogcmd", "idk", "Manage commands for OG users.", CommandCategory.STAFF, Roles.SUPER_ADMIN, true);
    this.aliases = new String[]{"uogcmd"};
  }

  @Override
  protected void executeCommand(CommandEvent event) {

  }
}
