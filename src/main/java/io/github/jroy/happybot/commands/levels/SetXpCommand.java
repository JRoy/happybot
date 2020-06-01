package io.github.jroy.happybot.commands.levels;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.levels.Leveling;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.lang3.StringUtils;

public class SetXpCommand extends CommandBase {

  private final Leveling leveling;

  public SetXpCommand(Leveling leveling) {
    super("setxp", "<user mention> <raw experience>", "Sets the target user's experience", CommandCategory.BOT, Roles.DEVELOPER);
    this.leveling = leveling;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (!e.isOwner()) {
      e.reply(C.permMsg(Roles.DEVELOPER));
      return;
    }

    if (e.getSplitArgs().length != 2 || !e.containsMention() || !StringUtils.isNumeric(e.getSplitArgs()[1])) {
      e.reply(invalid);
      return;
    }

    Member target = e.getMentionedMember();

    if (!leveling.isPastUser(target.getUser().getId())) {
      e.reply("User has never chatted, use !adduser");
      return;
    }

    leveling.setExp(target.getId(), Long.parseLong(e.getSplitArgs()[1]));
    e.reply("Set XP");
  }
}
