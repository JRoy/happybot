package io.github.jroy.happybot.commands.og;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.sql.og.OGActionType;
import io.github.jroy.happybot.sql.og.OGCommandManager;
import io.github.jroy.happybot.util.Roles;

public class SelfOgMngmtCommand extends CommandBase {

  @SuppressWarnings("FieldCanBeLocal")
  private final String USAGE = "**Usage:**\n" +
      "`^ogcmd request <command name> <command text>` - Requests an OG Custom Command\n" +
      "`^ogcmd edit <command text>` - Requests an edit to your OG Custom Command";

  private OGCommandManager ogCommandManager;

  public SelfOgMngmtCommand(OGCommandManager ogCommandManager) {
    super("ogcmd", "help", "Manage your custom og command!", CommandCategory.GENERAL, Roles.OG, true);
    this.aliases = new String[]{"myogcommand", "myogcmd"};
    this.ogCommandManager = ogCommandManager;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (e.getArgs().isEmpty() || e.getSplitArgs().length < 2) {
      e.reply(USAGE);
      return;
    }

    if (e.getSplitArgs()[0].equalsIgnoreCase("request") && e.getSplitArgs().length > 2) {
      if (ogCommandManager.hasCommand(e.getMember().getUser().getId())) {
        e.reply("You already have an OG Command! You can edit with `^ogcmd edit <command text>`");
        return;
      }

      if (ogCommandManager.isPendingAction(e.getMember().getUser().getId())) {
        e.reply("Your command is still pending approval!");
        return;
      }

      if (ogCommandManager.isCommand(e.getSplitArgs()[1])) {
        e.reply("That command name is already taken!");
        return;
      }

      ogCommandManager.requestCommand(OGActionType.COMMAND, null, e.getSplitArgs()[1], e.getArgs().replaceFirst("request " + e.getSplitArgs()[1], ""), e.getMember());
      e.reply("Command Requested, please don't bug staff to approve it!");
    } else if (e.getSplitArgs()[0].equalsIgnoreCase("edit")) {
      if (!ogCommandManager.hasCommand(e.getMember().getUser().getId())) {
        e.reply("You do not have a command, create one with `^ogcmd request`!");
        return;
      }

      int id = ogCommandManager.getCommandId(e.getMember().getUser().getId());
      ogCommandManager.requestCommand(OGActionType.CONTENT, id, ogCommandManager.getCommandFromId(id), e.getArgs().replaceFirst("edit ", ""), e.getMember());
      e.reply("Command Edit Requested, please don't bug staff to approve it!");
    } else {
      e.reply(USAGE);
    }
  }
}
