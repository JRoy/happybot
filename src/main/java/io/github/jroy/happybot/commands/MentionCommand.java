package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;

public class MentionCommand extends CommandBase {

  public MentionCommand() {
    super("mention", "<twitter/git/twitch>", "Toggles what messages you would like to mentioned for.", CommandCategory.GENERAL);
    this.guildOnly = false;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (!e.getArgs().isEmpty()) {
      StringBuilder sb = new StringBuilder();
      if (e.getArgs().contains("twitter")) {
        if (C.toggleRole(e.getMember(), Roles.TWITTER)) {
          sb.append("You will now receive notifications when happyheart tweets!").append("\n");
        } else {
          sb.append("You will no longer receive notifications when happyheart tweets!").append("\n");
        }
      }
      if (e.getArgs().contains("git")) {
        if (C.toggleRole(e.getMember(), Roles.GIT)) {
          sb.append("You will now receive notifications from git!").append("\n");
        } else {
          sb.append("You will no longer receive notifications from git!").append("\n");
        }
      }
      if (e.getArgs().contains("updates")) {
        if (C.toggleRole(e.getMember(), Roles.UPDATES)) {
          sb.append("You will now receive notifications for updates!").append("\n");
        } else {
          sb.append("You will no longer receive notifications for updates!").append("\n");
        }
      }
      if (e.getArgs().contains("twitch")) {
        if (C.toggleRole(e.getMember(), Roles.TWITCH)) {
          sb.append("You will now receive notifications for twitch streams!").append("\n");
        } else {
          sb.append("You will no longer receive notifications for twitch streams!").append("\n");
        }
      }
      if (sb.toString().isEmpty()) {
        e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
        return;
      }
      e.reply(sb.append("Applied requested roles!").toString());
    } else {
      e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
    }
  }
}
