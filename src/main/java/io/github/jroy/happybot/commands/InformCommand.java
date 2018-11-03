package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.Channels;
import io.github.jroy.happybot.util.Roles;

public class InformCommand extends CommandBase {

  public InformCommand() {
    super("inform", "<message>", "Pings the update role in #bot-updates", CommandCategory.STAFF, Roles.DEVELOPER);
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (e.getArgs().isEmpty()) {
      e.reply(invalid);
      return;
    }
    Roles.UPDATES.getRole().getManager().setMentionable(true).complete();
    Channels.BOT_UPDATES.getChannel().sendMessage("New Bot " + Roles.UPDATES.getRole().getAsMention() + ": \n" + e.getArgs()).complete();
    Roles.UPDATES.getRole().getManager().setMentionable(false).queue();
    e.reply("Message Sent!");
  }
}
