package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;

import java.util.concurrent.ThreadLocalRandom;

public class RandomSeasonCommand extends CommandBase {

  private String[] seasons;

  public RandomSeasonCommand() {
    super("randomseason", null, "Gives you a random happyheart season!", CommandCategory.FUN);
    seasons = new String[]{
        "Season 1 - <http://bit.ly/2y0akBf>",
        "Season 2 - <http://bit.ly/2kxqexc>",
        "Season 3 - <http://bit.ly/2xpJ3Zm>",
        "Season 4 - <http://bit.ly/2fTJKPD>",
        "Season 5 - <http://bit.ly/2xq8dCu>",
        "Season 6 - <https://bit.ly/2JiwVze>"
    };
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    int seasonIndex = ThreadLocalRandom.current().nextInt(seasons.length);
    e.reply(":game_die: Rolling the dice! :game_die:\n" + seasons[seasonIndex]);
  }
}
