package io.github.jroy.happybot.commands.levels;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.levels.Leveling;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.lang3.StringUtils;

public class AddUserCommand extends CommandBase {

  private Leveling leveling;

  public AddUserCommand(Leveling leveling) {
    super("adduser", "<user> <xp>", "Adds a user to the level database.", CommandCategory.BOT, Roles.DEVELOPER);
    this.leveling = leveling;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (e.getSplitArgs().length != 2 || !e.containsMention() || !StringUtils.isNumeric(e.getSplitArgs()[1])) {
      e.reply(invalid);
      return;
    }

    Member target = e.getMentionedMember();

    if (leveling.isPastUser(target.getUser().getId())) {
      e.reply("User is already inside the database!");
      return;
    }

    leveling.spawnUser(target.getUser().getId());
    leveling.addExp(target.getUser().getId(), Integer.parseInt(e.getSplitArgs()[1]));
    e.reply("Created User!");

  }
}
