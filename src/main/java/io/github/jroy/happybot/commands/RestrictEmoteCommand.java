package io.github.jroy.happybot.commands;

import com.google.common.collect.Sets;
import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Role;

import java.util.HashSet;
import java.util.Set;

public class RestrictEmoteCommand extends CommandBase {

  public RestrictEmoteCommand() {
    super("restrictemote", "<on/off> <emoteid/*>", "Restricts an emote to moderators only.", CommandCategory.STAFF, Roles.SUPER_ADMIN);
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (e.getSplitArgs().length != 2 || (!e.getSplitArgs()[0].equalsIgnoreCase("on") && !e.getSplitArgs()[0].equalsIgnoreCase("off"))) {
      e.replyError(invalid);
      return;
    }

    Emote emote = null;
    if (!e.getSplitArgs()[1].equals("*")) {
      emote = e.getGuild().getEmoteById(e.getSplitArgs()[1]);
      if (emote == null) {
        e.replyError("Invalid emote id!");
        return;
      }
    }


    Set<Role> roles;
    if (e.getSplitArgs()[0].equalsIgnoreCase("on")) {
      roles = Sets.newHashSet(Roles.MODERATOR.getRole());
    } else {
      roles = new HashSet<>();
    }

    if (emote != null) {
      emote.getManager().setRoles(roles).queue();
    } else {
      for (Emote emotes : e.getGuild().getEmotes()) {
        if (emotes.getName().equals("gild")) {
          //skip this one
          continue;
        }
        emotes.getManager().setRoles(roles).queue();
      }
    }
    e.reply("Updated emote state!");
  }
}
