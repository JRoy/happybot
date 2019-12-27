package io.github.jroy.happybot.commands.levels;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.levels.Leveling;
import io.github.jroy.happybot.levels.LevelingToken;
import io.github.jroy.happybot.util.C;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.util.HashMap;

public class OldLevelCommand extends CommandBase {

  private Leveling leveling;

  public OldLevelCommand(Leveling leveling) {
    super("oldrank", "<user>", "Displays your current rank stats.", CommandCategory.FUN);
    this.aliases = new String[]{"oldlevel", "orank", "olevel"};
    this.leveling = leveling;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    Member target = e.getMember();

    if (!e.getArgs().isEmpty()) {
      target = C.matchMember(target, e.getArgs());
    }

    String targetId = target.getUser().getId();

    if (!leveling.isPastUser(targetId)) {
      e.replyError("This user is not in the database!");
      return;
    }

    long totalXp = leveling.getExp(targetId);
    int level = leveling.toLevel(totalXp);
    int rankXp = leveling.getNextExp(level).intValue();
    int totalExpP = level - 1;
    if (level == 0) {
      totalExpP = 0;
    }
    long progressXp = totalXp - leveling.getTotalExp(totalExpP) - leveling.getNextExp(totalExpP).intValue();

    int rank = -1;
    for (HashMap.Entry<Integer, LevelingToken> curEntry : leveling.topCache.entrySet()) {
      if (curEntry.getValue().getMember().getUser().getId().equals(target.getUser().getId())) {
        rank = curEntry.getKey();
        break;
      }
    }

    EmbedBuilder builder = new EmbedBuilder();

    builder.setAuthor(target.getUser().getName(), null, target.getUser().getAvatarUrl());
    builder.setColor(target.getColor());

    builder.addField("Rank", (rank == -1 ? "??" : rank) + "/"+leveling.topCache.size(), false);
    builder.addField("Level", String.valueOf(level), false);
    builder.addField("Level Progress", C.prettyNum(progressXp) + "/" + C.prettyNum(rankXp) + " (" + C.prettyNum(totalXp) + " total)", false);

    e.reply(builder.build());
  }
}