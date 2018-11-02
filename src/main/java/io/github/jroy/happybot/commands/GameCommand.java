package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.game.Game;
import io.github.jroy.happybot.game.GameManager;
import io.github.jroy.happybot.game.GameType;
import io.github.jroy.happybot.game.model.PendingGameToken;

public class GameCommand extends CommandBase {
  private final GameManager gameManager;

  private static final String HELP_MESSAGE = "**Game Command Help:**\n" +
      "`^game create <game name>` - Creates a game and prompts people to join\n" +
      "`^game start` - Starts a game if you have enough players\n" +
      "`^game stop` - Stops a game\n" +
      "`^game list` - Lists all the possible game you can play";
//      "`^game spectate` - Joins a game lobby in spectate view";

  public GameCommand(GameManager gameManager) {
    super("game", "<create/start/stop/list/join/spectate> [<gameid/gametype>]", "Helper command for all things related to game.", CommandCategory.FUN);
    this.gameManager = gameManager;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (e.getArgs().isEmpty()) {
      e.replyError(HELP_MESSAGE);
      return;
    }
    switch (e.getSplitArgs()[0]) {
      case "create": {
        if (gameManager.isPendingRestart()) {
          e.reply("Happybot is pending a restart, game commands have been paused while this is happening.");
          return;
        }

        if (e.getSplitArgs().length < 2) {
          e.replyError(HELP_MESSAGE);
          return;
        }
        if (!GameType.isGame(e.getSplitArgs()[1].toUpperCase())) {
          e.replyError("Invalid Game! For a list of game IDs use `^game list`");
          return;
        }

        if (gameManager.isActive(e.getMember().getUser().getId())) {
          e.replyError("You are already active in the game system! You must be out of a game and not pending in a game to create one.");
          return;
        }

        GameType gameType = GameType.valueOf(e.getSplitArgs()[1].toUpperCase());

        if (!e.hasRole(gameType.getRequiredRole())) {
          e.replyError("You do not have permission to create this game!");
          return;
        }

        try {
          gameManager.pendGame(e.getMessage(), e.getMember(), gameType.getGameClass()
              .getConstructor(GameManager.class).newInstance(gameManager));
        } catch (ReflectiveOperationException ex) {
          e.replyError("Could not create game: " + ex.getMessage() + "\nNo action has occurred.");
          ex.printStackTrace();
        }
        break;
      }
      case "start": {
        if (gameManager.isPendingRestart()) {
          e.reply("Happybot is pending a restart, game commands have been paused while this is happening.");
          return;
        }

        if (gameManager.isPendingUser(e.getMember().getUser().getId())) {
          PendingGameToken token = gameManager.getPendingToken(e.getMember());
          if (token.getPlayers().size() < token.getGame().getMinPlayers()) {
            e.replyError("You do not have the minimum amount of required to start the game.");
            return;
          }
          gameManager.startGame(e.getMember());
        } else {
          e.replyError("You do not have a pending game.");
          return;
        }
        break;
      }
      case "stop": {
        if (gameManager.isHosting(e.getMember().getUser().getId())) {
          gameManager.stopGame(e.getMember());
          e.reply("Stopped the Game!");
        } else {
          e.replyError("You are not hosting a game!");
          return;
        }
        break;
      }
      case "list": {
        StringBuilder sb = new StringBuilder();
        sb.append("**Game List:**\n");
        for (GameType curType : GameType.values()) {
          if (!curType.isDisplayGame()) {
            continue;
          }
          try {
            Game curGame = curType.getGameClass().getConstructor(GameManager.class).newInstance(gameManager);
            sb.append("**").append(curGame.getName()).append("** (ID: ").append(curType.name()).append(") - ").append(curGame.getDescription()).append(" | (").append(curGame.getMinPlayers()).append(" Min, ").append(curGame.getMaxPlayers()).append(" Max)\n");
          } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
          }

        }
        e.reply(sb.toString());
        break;
      }
      case "join": {
        break;
      }
      case "spectate": {
        break;
      }
      default: {
        e.replyError(HELP_MESSAGE);
        break;
      }
    }

  }
}
