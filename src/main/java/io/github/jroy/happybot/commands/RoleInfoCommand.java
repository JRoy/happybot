package io.github.jroy.happybot.commands;

import com.google.common.collect.ImmutableMap;
import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.theme.DiscordThemerImpl;
import io.github.jroy.happybot.util.C;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;

import java.util.Map;

public class RoleInfoCommand extends CommandBase {
  private static final String NORMAL_THEME = "normal";
  private static final ImmutableMap<Object, Object> THEMES = ImmutableMap.builder()
      .put("valentines", "Valentine's")
      .put("winter", "Winter")
      .put("fool", "April Fool's")
      .put("spooky", "Halloween")
      .build();

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

    Map<String, String> names = themer.getRoleNames(role.getId());
    String normal = names.get(NORMAL_THEME);
    if (normal == null) {
      normal = role.getName();
    }
    EmbedBuilder embed = new EmbedBuilder()
        .setTitle(normal)
        .setFooter("ID: " + role.getId(), null)
        .setColor(C.randomColour());

    for (Map.Entry<String, String> entry : names.entrySet()) {
      String name = (String) THEMES.get(entry.getKey());
      if (name != null) {
        embed.addField(name, entry.getValue(), true);
      }
    }

    if (embed.getFields().size() == 0) {
      embed.setDescription("This role has no themed names.");
    }

    event.reply(embed.build());
  }
}
