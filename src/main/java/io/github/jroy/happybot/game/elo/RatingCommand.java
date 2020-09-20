package io.github.jroy.happybot.game.elo;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.game.GameType;
import java.awt.Color;
import java.util.List;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

public class RatingCommand extends CommandBase {

  private final EloManager eloManager;

  public RatingCommand(EloManager eloManager) {
    super("rating", "[leaderboard]", "Shows your rating in games.", CommandCategory.FUN);
    this.aliases = new String[]{"elo"};
    this.eloManager = eloManager;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (e.getArgs().equalsIgnoreCase("leaderboard") || e.getArgs().equalsIgnoreCase("lb")) {
      EmbedBuilder builder = new EmbedBuilder()
          .setTitle("Leaderboard")
          .setColor(Color.PINK);

      for (GameType gameType : GameType.values()) {
        if (gameType.isSuportsElo()) {
          List<EloPlayer> players = eloManager.getTop10(gameType);

          StringBuilder sb = new StringBuilder();
          for (int i = 0; i < players.size(); i++) {
            if (i > 0) {
              sb.append("\n");
            }
            EloPlayer player = players.get(i);
            User userById = e.getJDA().getUserById(player.getPlayer());
            sb.append("#").append(i+1).append(" *")
                .append(userById == null ? player.getPlayer() : userById.getAsTag())
                .append("* - **").append((int) player.getEloRating()).append("**");
          }

          builder.addField(gameType.getName() + " top 10", sb.toString(), false);
        }
      }

      e.reply(builder.build());
    } else {
      EmbedBuilder builder = new EmbedBuilder()
          .setAuthor(e.getAuthor().getName(), null, e.getAuthor().getAvatarUrl())
          .setTitle("My ratings")
          .setColor(Color.RED);

      for (GameType gameType : GameType.values()) {
        if (gameType.isSuportsElo()) {
          EloKey key = new EloKey(e.getMember().getId(), gameType);
          EloPlayer player = eloManager.getPlayer(key);
          int rankBits = eloManager.getRank(key);

          int rank = rankBits & 0xFF;
          int total = rankBits >> 8;

          builder.addField(gameType.getName(), "**Rating:** " + (int) player.getEloRating()
              + (rankBits == -1 ? "\n*Unranked*" : "\n**Ranking**: #" + rank + "/" + total), true);
        }
      }

      e.reply(builder.build());
    }
  }
}