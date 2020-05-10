package io.github.jroy.happybot.commands.og;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.sql.og.OGAction;
import io.github.jroy.happybot.sql.og.OGActionType;
import io.github.jroy.happybot.sql.og.OGCommandManager;
import io.github.jroy.happybot.util.Roles;

public class SelfOgMngmtCommand extends CommandBase {

  @SuppressWarnings("FieldCanBeLocal")
  private final String USAGE = "**Usage:**\n" +
      "`^ogcmd request <command name> <command text>` - Requests an OG Custom Command\n" +
      "`^ogcmd edit <command text>` - Requests an edit to your OG Custom Command\n" +
      "`^ogcmd edit-name <command name>` - Requests an edit to your OG Custom Command's name";

  private final OGCommandManager ogCommandManager;

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
      if (ogCommandManager.hasCommand(e.getMember().getId())) {
        e.reply("You already have an OG Command! You can edit with `^ogcmd edit <command text>`");
        return;
      }

      if (ogCommandManager.isPendingAction(e.getMember().getId())) {
        e.reply("Your command is still pending approval!");
        return;
      }

      if (ogCommandManager.isCommand(e.getSplitArgs()[1])) {
        e.reply("That command name is already taken!");
        return;
      }

      if (ogCommandManager.requestCommand(
          new OGAction(OGActionType.COMMAND, e.getMember().getId(), null, e.getSplitArgs()[1], e.getArgs().replaceFirst("request " + e.getSplitArgs()[1], "")))) {
        e.reply("Successfully added command.");
      } else {
        e.reply("Command requested, please don't bug staff to approve it!");
      }
    } else if (e.getSplitArgs()[0].equalsIgnoreCase("edit")) {
      if (!ogCommandManager.hasCommand(e.getMember().getId())) {
        e.reply("You do not have a command, create one with `^ogcmd request`!");
        return;
      }

      int id = ogCommandManager.getCommandId(e.getMember().getUser().getId());
      if (ogCommandManager.requestCommand(
          new OGAction(OGActionType.CONTENT, e.getMember().getId(), id, ogCommandManager.getCommandFromId(id), e.getArgs().replaceFirst("edit ", "")))) {
        e.reply("Successfully edited command.");
      } else {
        e.reply("Command edit requested, please don't bug staff to approve it!");
      }
    } else if (e.getSplitArgs()[0].equalsIgnoreCase("edit-name")) {
      if (!ogCommandManager.hasCommand(e.getMember().getUser().getId())) {
        e.reply("You do not have a command, create one with `^ogcmd request`!");
        return;
      }

      int id = ogCommandManager.getCommandId(e.getMember().getUser().getId());
      if (ogCommandManager.requestCommand(
          new OGAction(OGActionType.NAME, e.getMember().getId(), id, ogCommandManager.getCommandFromId(id) + "|" + e.getSplitArgs()[1], ogCommandManager.getCommandContentFromId(id)))) {
        e.reply("Successfully edited command name.");
      } else {
        e.reply("Command name edit requested.");
      }
    } else {
      e.reply(USAGE);
    }
  }
}
