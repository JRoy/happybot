package io.github.jroy.happybot.commands.levels;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.levels.Leveling;
import io.github.jroy.happybot.levels.LevelingToken;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;
import io.github.jroy.happybot.util.TextGeneration;
import net.dv8tion.jda.api.entities.Member;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;

public class LevelCommand extends CommandBase {

  private final Leveling leveling;

  public LevelCommand(Leveling leveling) {
    super("rank", "<user> OR <set-card>", "Displays your current rank stats.", CommandCategory.FUN);
    this.aliases = new String[]{"level"};
    this.leveling = leveling;
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  protected void executeCommand(CommandEvent e) {

    new OldLevelCommand(leveling).executeCommand(e);

    if (true) {
      return;
    }

    Member target = e.getMember();

    if (!e.getArgs().isEmpty()) {
      if (e.getArgs().equalsIgnoreCase("set-card")) {
        if (!e.hasRole(Roles.LEGENDARY)) {
          e.replyError(C.permMsg(Roles.LEGENDARY));
          return;
        }
        if (e.getMessage().getAttachments().size() < 1 || !e.getMessage().getAttachments().get(0).isImage()) {
          e.replyError("You must attach an your background image to this command!");
          return;
        }
        try {
          leveling.setImage(e.getMember().getUser().getId(), TextGeneration.resize(ImageIO.read(new URL(e.getMessage().getAttachments().get(0).getUrl())), 916, 3036));
          e.reply("Updated your rank card!");
          return;
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
      target = C.matchMember(target, e.getArgs());
    }

    String targetId = target.getUser().getId();

    if (!leveling.isPastUser(targetId)) {
      e.replyError("This user is not in the database!");
      return;
    }

    long totalXp = leveling.getExp(targetId);
    int level = leveling.toLevel(totalXp);
    int rankXp = leveling.getNextExp(level).intValue();
    int totalExpP = level - 1;
    if (level == 0) {
      totalExpP = 0;
    }
    long progressXp = totalXp - leveling.getTotalExp(totalExpP) - leveling.getNextExp(totalExpP).intValue();

    int rank = -1;
    for (HashMap.Entry<Integer, LevelingToken> curEntry : leveling.topCache.entrySet()) {
      if (curEntry.getValue().getMember().getUser().getId().equals(target.getUser().getId())) {
        rank = curEntry.getKey();
        break;
      }
    }

    try {
      BufferedImage card = TextGeneration.background;
      if (leveling.hasImage(targetId)) {
        card = leveling.getImage(targetId);
      }
      card = TextGeneration.writeImage(card, TextGeneration.card, 0, 0);
      card = TextGeneration.writeImage(card, TextGeneration.calculateProgressId(progressXp, rankXp), 0, 0);
      card = TextGeneration.writeImage(card, TextGeneration.resize(TextGeneration.circleize(ImageIO.read(new URL(Objects.requireNonNull(target.getUser().getAvatarUrl())))), 255, 255), 1450, 80);
      card = TextGeneration.writeTextCenter(card, C.getFullName(target.getUser()), target.getColor(), 200F, 0, 100);
      card = TextGeneration.writeTextCenter(card, C.prettyNum(totalXp), target.getColor(), 75F, 700, 220);
      card = TextGeneration.writeText(card, (rank == -1 ? "?" : String.valueOf(rank)), target.getColor(), 125F, 1000, 210);
      card = TextGeneration.writeText(card, String.valueOf(level), target.getColor(), 125F, 2320, 210);
      card = TextGeneration.writeTextCenter(card, C.prettyNum(progressXp), target.getColor(), 75F, -255, 220);
      card = TextGeneration.writeTextCenter(card, C.prettyNum(rankXp), target.getColor(), 75F, 10, 220);
      ByteArrayOutputStream os = new ByteArrayOutputStream();

      ImageIO.write(card, "png", os);
      //noinspection ConfusingArgumentToVarargsMethod
      e.getChannel().sendFile(new ByteArrayInputStream(os.toByteArray()), "rank.png", null).queue();
    } catch (IOException e1) {
      e1.printStackTrace();
      e.reply("lol no :heart:");
    }
  }
}
