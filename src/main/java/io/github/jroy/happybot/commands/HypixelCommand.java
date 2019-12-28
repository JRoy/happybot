package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.apis.Hypixel;
import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.C;
import me.kbrewster.exceptions.APIException;
import me.kbrewster.hypixelapi.player.HypixelPlayer;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.HashMap;

public class HypixelCommand extends CommandBase {
  private static final String HYPIXEL_IMAGE = "https://media-curse.cursecdn.com/attachments/264/727/f7c76fdb4569546a9ddf0e58c8653823.png";

  private Hypixel hypixel;

  public HypixelCommand(Hypixel hypixel) {
    super("hypixel", "<player>", "Checks target's hypixel player stats!", CommandCategory.FUN);
    this.name = "hypixel";
    this.help = "Check your hypixel stats!";
    this.arguments = "<player>";
    this.category = new Category("Fun");
    this.hypixel = hypixel;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (e.getArgs().isEmpty()
        || C.containsMention(e)) {
      e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
      return;
    }
    if (hypixel.isValidPlayer(e.getArgs())) {
      try {
        HypixelPlayer player = hypixel.getPlayer(e.getArgs());
        EmbedBuilder embed = new EmbedBuilder()
            .setTitle("Hypixel Player Statistics")
            .setDescription("Listing statistics:")
            .setFooter("Stats provided by Hypixel's API", HYPIXEL_IMAGE);
        for (HashMap.Entry<String, String> entry : hypixel.getAllFields(player).entrySet()) {
          if (entry.getValue() != null && !entry.getValue().equals("0")) {
            embed.addField(C.bold(entry.getKey()), entry.getValue(), true);
          }
        }
        e.reply(embed.build());
      } catch (APIException e1) {
        e.replyError("The API had an error or the player was invalid.");
      }
    } else {
      e.replyError("The API had an error or the player was invalid.");
    }
  }
}
