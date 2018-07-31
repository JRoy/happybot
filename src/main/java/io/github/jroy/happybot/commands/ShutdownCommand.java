package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Logger;
import io.github.jroy.happybot.util.Roles;

public class ShutdownCommand extends CommandBase {

  public ShutdownCommand() {
    super("shutdown", "<instance/vm>", "Shutdowns the bot with care, concern, and love <3.", CommandCategory.BOT, Roles.DEVELOPER);
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (e.getArgs().startsWith("instance")) {
      e.getMessage().addReaction("✅").queue();
      e.replySuccess(":white_check_mark: Stopping the JDA Instance!");
      e.getJDA().shutdown();
      Logger.log("The JDA instance has been shutdown.");
    } else if (e.getArgs().startsWith("vm")) {
      e.getMessage().addReaction("✅").queue();
      e.replySuccess(":white_check_mark: Stopping the VM!");
      e.getJDA().shutdown();
      Logger.log("The JDA instance has been shutdown...exiting the program.");
      System.exit(0);
    } else {
      e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
    }
  }

}