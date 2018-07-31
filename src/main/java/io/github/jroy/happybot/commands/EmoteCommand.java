package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.core.entities.Icon;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class EmoteCommand extends CommandBase {

  public EmoteCommand() {
    super("emote", "<emote name> <staff only> <image url>", "Creates an emote", CommandCategory.STAFF, Roles.SUPER_ADMIN);
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (e.getSplitArgs().length < 3) {
      e.replyError(invalid);
      return;
    }

    if (!C.containsBool(e.getSplitArgs()[1])) {
      e.replyError(invalid);
      return;
    }

    String name = e.getSplitArgs()[0];
    boolean staffOnly = Boolean.getBoolean(e.getSplitArgs()[1]);
    URL url;
    try {
      url = new URL(e.getSplitArgs()[2]);
    } catch (MalformedURLException e1) {
      e.replyWarning("Invalid URL!");
      return;
    }
    try {
      if (staffOnly) {
        C.getGuildCtrl().createEmote(name, Icon.from(url.openStream()), Roles.HELPER.getRole()).queue();
      } else {
        C.getGuildCtrl().createEmote(name, Icon.from(url.openStream())).queue();
      }
      e.replySuccess("Created emote!");
    } catch (IOException e1) {
      e.replyError("Error while creating emote: " + e1.getMessage());
    }

  }
}
