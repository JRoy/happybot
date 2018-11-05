package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.game.GameManager;
import io.github.jroy.happybot.util.Roles;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShutdownCommand extends CommandBase {

  private final GameManager gameManager;

  public ShutdownCommand(GameManager gameManager) {
    super("kys", null, "Shutdowns the bot with care, concern, and love <3.", CommandCategory.BOT, Roles.SUPER_ADMIN);
    this.aliases = new String[]{"shutdown"};
    this.gameManager = gameManager;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    boolean confirm = e.getArgs().contains("-y") || e.getArgs().contains("--yes");
    boolean force =  e.getArgs().contains("-f") || e.getArgs().contains("--force");
    if (!confirm) {
      e.reply("The `^kys` command will instantly restart the bot, please do `^kys -y` to confirm this action.");
      return;
    }

    if (gameManager.isGamesActive() && !force) {
      e.reply("There are currently games active, I have impended a shutdown and will restart when all games close!");
      gameManager.setPendingRestart(true);
      return;
    }
    e.replySuccess(":white_check_mark: Restarting!");
    e.getJDA().shutdown();
    log.warn("The JDA instance has been shutdown...exiting the program.");
    System.exit(0);
  }

}