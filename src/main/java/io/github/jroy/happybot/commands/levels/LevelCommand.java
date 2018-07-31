package io.github.jroy.happybot.commands.levels;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.levels.Leveling;
import io.github.jroy.happybot.levels.LevelingToken;
import io.github.jroy.happybot.util.C;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;

import java.util.HashMap;

public class LevelCommand extends CommandBase {

  private Leveling leveling;

  public LevelCommand(Leveling leveling) {
    super("rank", "<user>", "Displays your current rank stats.", CommandCategory.FUN);
    this.aliases = new String[]{"level"};
    this.leveling = leveling;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    Member target = e.getMember();

    if (!e.getArgs().isEmpty()) {
      Member fromName = C.getMemberFromName(e.getArgs());
      if (e.containsMention()) {
        target = e.getMentionedMember();
      } else if (fromName != null) {
        target = fromName;
      }
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

    builder.addField("Rank", (rank == -1 ? "??" : rank) + "/25", false);
    builder.addField("Level", String.valueOf(level), false);
    builder.addField("Level Progress", C.prettyNum((int) progressXp) + "/" + C.prettyNum(rankXp) + " (" + C.prettyNum((int) totalXp) + " total)", false);

    e.reply(builder.build());
  }
}
