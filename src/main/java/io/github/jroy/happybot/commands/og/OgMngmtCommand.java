package io.github.jroy.happybot.commands.og;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.sql.og.OGCommandManager;
import io.github.jroy.happybot.util.Roles;

public class OgMngmtCommand extends CommandBase {

  private final OGCommandManager ogCommandManager;

  public OgMngmtCommand(OGCommandManager ogCommandManager) {
    super("userogcmd", "idk", "Manage commands for OG users.", CommandCategory.STAFF, Roles.SUPER_ADMIN, true);
    this.aliases = new String[]{"uogcmd"};
    this.ogCommandManager = ogCommandManager;
  }

  @Override
  protected void executeCommand(CommandEvent event) {

  }
}
