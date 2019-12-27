package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.events.star.StarMessages;
import io.github.jroy.happybot.events.star.StarStatsToken;
import io.github.jroy.happybot.util.C;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

public class SelfStarCommands extends CommandBase {

  private StarMessages starMessages;

  public SelfStarCommands(StarMessages starMessages) {
    super("mystars", null, "Shows your star/gild/heel messages counts.", CommandCategory.FUN);
    this.aliases = new String[]{"mygilds", "myheels"};
    this.starMessages = starMessages;

  }

  @Override
  protected void executeCommand(CommandEvent e) {
    User target;
    if (e.containsMention()) {
      target = e.getMentionedMember().getUser();
    } else {
      target = e.getMember().getUser();
    }
    StarStatsToken token = starMessages.getUser(target.getId());

    e.reply(new EmbedBuilder()
        .setTitle(C.getFullName(target) + "'s Star Statistics")
        .setDescription("Here are " + C.getFullName(target) + "'s statistics for starred, gilded, and heeled messages.")
        .setFooter("Stats provided by happybot's SQLManager", e.getSelfUser().getAvatarUrl())
        .addField(C.bold("Starred Messages:"), token.getStarCount() + " stars", false)
        .addField(C.bold("Gilded Messages:"), token.getGildCount() + " gilds", false)
        .addField(C.bold("Heeled Messages:"), token.getHeelCount() + " heels", false)
        .addField(C.bold("Total:"), String.valueOf(token.getGildCount() + token.getStarCount() + token.getHeelCount()), false)
        .build());
  }
}
