package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.levels.Leveling;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.api.entities.Member;

public class FansCommand extends CommandBase {

  private final Leveling leveling;

  public FansCommand(Leveling leveling) {
    super("fixfans", null, "Fixes broken users with no Fans Role", CommandCategory.STAFF, Roles.SUPER_ADMIN, true);
    this.leveling = leveling;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    e.getChannel().sendTyping().queue();
    int affected = 0;
    int levels = 0;
    for (Member curM : C.getGuild().getMembers()) {
      if (!C.hasRole(curM, Roles.FANS) && !curM.getUser().isBot()) {
        C.giveRole(curM, Roles.FANS);
        affected++;
      }

      if (leveling.isPastUser(curM.getUser().getId())) {
        int level = leveling.toLevel(leveling.getExp(curM.getId()));
        Roles[] roles;
        if (level >= 65) {
          roles = new Roles[]{Roles.LEGENDARY, Roles.OG, Roles.OBSESSIVE, Roles.TRYHARD, Roles.REGULAR};
        } else if (level >= 50) {
          roles = new Roles[]{Roles.OG, Roles.OBSESSIVE, Roles.TRYHARD, Roles.REGULAR};
        } else if (level >= 30) {
          roles = new Roles[]{Roles.OBSESSIVE, Roles.TRYHARD, Roles.REGULAR};
        } else if (level >= 20) {
          roles = new Roles[]{Roles.TRYHARD, Roles.REGULAR};
        } else if (level >= 10) {
          roles = new Roles[]{Roles.REGULAR};
        } else {
          roles = new Roles[]{};
        }
        if (!C.hasRoles(curM, roles)) {
          C.giveRoles(curM, roles);
          levels++;
        }
      }
    }
    e.replySuccess("All Done!\n" + affected + " Fan Users Affected!\n" + levels + " Level Rewards Affected!");
  }
}
