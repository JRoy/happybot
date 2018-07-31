package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Channels;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;

import java.awt.*;

public class BanCommand extends CommandBase {
  public BanCommand() {
    super("ban", "<user mention> <reason>", "Bans target user from server.", CommandCategory.STAFF, Roles.MODERATOR);
    this.aliases = new String[]{"begone"};
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (C.containsMention(e)) {
      if (e.getArgs().replaceAll("<(.*?)>", "").isEmpty()) {
        e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
        return;
      }
      String reason = e.getArgs().replaceFirst("<(.*?)> ", "");
      Member target = C.getMentionedMember(e);

      if (target.getUser().getId().equalsIgnoreCase(e.getMember().getUser().getId())) {
        e.replyError("You may not ban yourself! :wink:");
        return;
      }

      C.privChannel(target, "Banned with Reason: " + reason);

      C.getCtrl(e).ban(target.getUser(), 7, "Banned by Moderator: " + e.getMember().getUser().getName()).reason("Banned by Moderator: " + e.getMember().getUser().getName() + ". With Reason: " + reason).queue();
      e.replySuccess("User " + C.getFullName(target.getUser()) + " has been **FRIGGING BANNED** by " + e.getMember().getEffectiveName());
      Channels.LOG.getChannel().sendMessage(new EmbedBuilder()
          .setAuthor(C.getFullName(e.getMember().getUser()), null, e.getMember().getUser().getAvatarUrl())
          .setColor(Color.RED)
          .setThumbnail(target.getUser().getAvatarUrl())
          .setDescription(":information_source: **User Banned**\n" + C.bold("Banned " + target.getUser().getName() + "#" + target.getUser().getDiscriminator()) + "\n:page_facing_up: " + C.bold("Reason: ") + reason)
          .build()).queue();
    } else {
      e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
    }
  }
}
