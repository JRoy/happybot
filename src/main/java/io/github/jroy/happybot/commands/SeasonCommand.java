package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;

public class SeasonCommand extends CommandBase {

  public SeasonCommand() {
    super("seasons", null, "List's the seasons from happyheart's YouTube Channel.", CommandCategory.GENERAL);
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    e.replySuccess("**Happyheart Seasons:**\n\n" +
        "Season 1 - <http://bit.ly/2y0akBf>\n" +
        "Season 2 - <http://bit.ly/2kxqexc>\n" +
        "Season 3 - <http://bit.ly/2xpJ3Zm>\n" +
        "Season 4 - <http://bit.ly/2fTJKPD>\n" +
        "Season 5 - <http://bit.ly/2xq8dCu>\n" +
        "Season 6 - <https://bit.ly/2JiwVze>\n " +
        "Season 8 - <https://bit.ly/3gn6iFb>\n" +
        "Season 9 - <https://bit.ly/3jblsiH>");
  }
}
