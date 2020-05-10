package io.github.jroy.happybot.commands.levels;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.levels.Leveling;
import io.github.jroy.happybot.levels.LevelingToken;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Channels;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.sql.SQLException;
import java.util.Map;

public class LeaderboardCommand extends CommandBase {

  private final Leveling leveling;

  public LeaderboardCommand(Leveling leveling) {
    super("leaderboard", null, "Displays the level rankings.", CommandCategory.FUN);
    this.aliases = new String[]{"levels"};
    this.leveling = leveling;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (!e.hasRole(Roles.DEVELOPER)) {
      e.reply("This is a developer only command! If you are trying to view the leaderboard, check-out: " + Channels.LEADERBOARD.getChannel().getAsMention());
      return;
    }

    try {
      Map<Integer, LevelingToken> result = leveling.getTop(10);
      int curPos = 1;
      EmbedBuilder builder = new EmbedBuilder();
      builder.setAuthor("Top 10 XP Betrayals", null, e.getGuild().getIconUrl()).setFooter("Stats provided by happybot's Leveling API!", e.getJDA().getSelfUser().getAvatarUrl()).setColor(Color.MAGENTA).setDescription("Here are the current top 10 rankings for experience.");
      for (Map.Entry<Integer, LevelingToken> mapToken : result.entrySet()) {
        LevelingToken token = mapToken.getValue();
        builder.addField("- #" + curPos + ": " + C.getFullName(token.getMember().getUser()), "Level " + token.getLevel() + " (" + C.prettyNum((int) token.getExp()) + " XP)", false);
        curPos++;
      }
      e.reply(builder.build());
    } catch (SQLException e1) {
      e.replyError("Oof Error: " + e1.getMessage());
    }
  }
}
