package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.theme.DiscordThemerImpl;
import io.github.jroy.happybot.util.C;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Role;

import java.util.Map;

public class RoleInfoCommand extends CommandBase {
  private final DiscordThemerImpl themer;

  public RoleInfoCommand(DiscordThemerImpl themer) {
    super("role", "<role name>", "Show information on themed roles", CommandCategory.GENERAL);
    this.aliases = new String[] {"roleinfo", "name", "rolename", "rolehelp"};
    this.themer = themer;
  }

  @Override
  protected void executeCommand(CommandEvent event) {
    Role role = C.matchRole(event.getArgs());
    if (role == null) {
      event.replyError("Role not found!");
      return;
    }

    try {
      EmbedBuilder embed = new EmbedBuilder()
          .setTitle(role.getName())
          .setColor(C.randomColour());
      StringBuilder description = new StringBuilder();
      Map<String, String> names = themer.getRoleNames(role.getId());
      for (Map.Entry<String, String> entry : names.entrySet()) {
        if (description.length() > 0) {
          description.append("\n");
        }

        description.append("Theme ").append(C.bold(entry.getKey())).append(", name: ").append(entry.getValue());
      }
      embed.setDescription(description);

      event.reply(embed.build());
    } catch (Exception e) {
      event.replyError(e.getMessage());
      e.printStackTrace();
    }
  }
}
