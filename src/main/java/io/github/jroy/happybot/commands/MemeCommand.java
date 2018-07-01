package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.apis.reddit.MemePost;
import io.github.jroy.happybot.apis.reddit.Reddit;
import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.C;
import net.dv8tion.jda.core.EmbedBuilder;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class MemeCommand extends CommandBase {

    private final Reddit reddit;

    private List<String> subs = new ArrayList<>();

    public MemeCommand(Reddit reddit) {
        super("meme", "parsing...", "Displays a random meme from the requested subreddit.", CommandCategory.FUN);
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
        StringBuilder sb = new StringBuilder();
        for (String str : subs)
            sb.append(str).append("/");
        sb.setLength(sb.length() - 1);
        this.arguments = "<" + sb.toString() + ">";
        this.setCooldown(5, ChronoUnit.MINUTES);
    }


    @Override
    protected void executeCommand(CommandEvent e) {
        if (!subs.contains(e.getArgs().toLowerCase())) {
            e.replyError(invalid);
            return;
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
}
