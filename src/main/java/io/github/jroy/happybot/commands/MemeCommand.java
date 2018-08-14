package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.apis.reddit.MemePost;
import io.github.jroy.happybot.apis.reddit.Reddit;
import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;
import io.github.jroy.happybot.util.RuntimeEditor;
import net.dv8tion.jda.core.EmbedBuilder;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class MemeCommand extends CommandBase {

  private final Reddit reddit;

  private List<String> subs = new ArrayList<>();

  public MemeCommand(Reddit reddit) {
    super("meme", "<subreddit>", "Displays a random meme from the requested subreddit.", CommandCategory.FUN);
    this.reddit = reddit;
    this.aliases = new String[]{"memes", "reddit"};
    subs.add("me_irl");
    subs.add("memes");
    subs.add("deepfriedmemes");
    subs.add("programmerhumor");
    subs.add("pewdiepiesubmissions");
    subs.add("dankmemes");
    subs.add("woooosh");
    subs.add("happyheart");
    subs.add("hmm");
    subs.add("hmmm");
    subs.add("funny");
    subs.add("peoplefuckingdying");
    subs.add("bonehurtingjuice");
    subs.add("iamverysmart");
    subs.add("niceguys");
    subs.add("nicegirls");
    this.setCooldown(2, ChronoUnit.MINUTES);
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (!subs.contains(e.getArgs().toLowerCase())) {
      if (!RuntimeEditor.isAllowStaffSubBypass()) {
        helpMsg(e);
        return;
      } else {
        if (!e.hasRole(Roles.DEVELOPER) && !e.getArgs().isEmpty()) {
          helpMsg(e);
          return;
        }
      }
    }

    e.getChannel().sendTyping().queue();

    MemePost post = reddit.getRandomMedia(e.getArgs());
    String subreddit = "r/" + post.getSubreddit();

    EmbedBuilder eb = new EmbedBuilder()
        .setAuthor("Meme from " + subreddit, null, "https://www.redditstatic.com/desktop2x/img/favicon/android-icon-192x192.png")
        .setTitle(post.getTitle(), post.getPermaLink())
        .setFooter("Requested by: " + C.getFullName(e.getAuthor()), e.getAuthor().getAvatarUrl());
    if (post.isSelfPost()) {
      eb.setDescription("Here is your random meme selected from " + subreddit + "\n"
          + "**Self Post:**\n"
          + post.getSelfText());
    } else {
      eb.setDescription("Here is your random meme selected from " + subreddit)
          .setImage(post.getMediaUrl());
    }
    e.reply(eb.build());
  }

  private void helpMsg(CommandEvent e) {
    StringBuilder sb = new StringBuilder();
    for (String str : subs) {
      sb.append(str).append("/");
    }
    sb.setLength(sb.length() - 1);
    e.replyError(invalid.replace("subreddit", sb.toString()));
    removeFromCooldown(e.getMember());
  }
}
