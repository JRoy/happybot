package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.C;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

public class AvatarCommand extends CommandBase {

  public AvatarCommand() {
    super("avatar", "[<user>]", "Gives you the avatar of a user.", CommandCategory.FUN);
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    User target = e.getMember().getUser();

    if (!e.getArgs().isEmpty()) {
      target = C.matchMember(e.getMember(), e.getArgs()).getUser();
    }

    e.reply(new EmbedBuilder().setAuthor(C.getFullName(target), target.getAvatarUrl(), target.getEffectiveAvatarUrl())
        .setDescription(C.bold("Avatar"))
        .setImage(target.getAvatarUrl()).build());
  }
}
