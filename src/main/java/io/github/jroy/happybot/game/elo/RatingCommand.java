package io.github.jroy.happybot.game.elo;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.game.GameType;
import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;

public class RatingCommand extends CommandBase {

  private final EloManager eloManager;

  public RatingCommand(EloManager eloManager) {
    super("rating", null, "Shows your rating in games.", CommandCategory.FUN);
    this.aliases = new String[]{"elo"};
    this.eloManager = eloManager;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    EmbedBuilder builder = new EmbedBuilder()
        .setAuthor(e.getAuthor().getName(), null, e.getAuthor().getAvatarUrl())
        .setTitle("My ratings")
        .setColor(Color.RED);

    for (GameType gameType : GameType.values()) {
      if (gameType.isSuportsElo()) {
        EloKey key = new EloKey(e.getMember().getId(), gameType);
        EloPlayer player = eloManager.getPlayer(key);
        int rank = eloManager.getRank(key);
        builder.addField(gameType.getName(), "**Rating:** " + (int) player.getEloRating()
            + (rank == -1 ? "\n**Unranked**" : "\n**Ranking**: #" + rank), true);
      }
    }

    e.reply(builder.build());
  }
}