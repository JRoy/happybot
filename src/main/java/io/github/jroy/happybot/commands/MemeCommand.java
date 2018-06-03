package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.apis.reddit.MemePost;
import io.github.jroy.happybot.apis.reddit.Reddit;
import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;

public class MemeCommand extends CommandBase {

    private final Reddit reddit;

    private ArrayList<String> subs = new ArrayList<>();
    private HashMap<Member, OffsetDateTime> cooldowns = new HashMap<>();

    public MemeCommand(Reddit reddit) {
        super("meme", "parsing...", "Displays a random meme from the requested subreddit.", CommandCategory.FUN);
        this.reddit = reddit;
        subs.add("me_irl");
        subs.add("memes");
        subs.add("deepfriedmemes");
        subs.add("ProgrammerHumor");
        subs.add("pewdiepiesubmissions");
        subs.add("dankmemes");
        subs.add("woooosh");
        this.arguments = "<" + createArgs() + ">";
    }


    @Override
    protected void executeCommand(CommandEvent e) {
        if (!e.hasRole(Roles.DEVELOPER) && cooldowns.containsKey(e.getMember())) {
            int time = getTimeRemaining(e.getMember());
            if (time > 0) {
                e.replyError("You must wait " + String.valueOf(time) + " seconds before preforming this again!");
                return;
            }
        }

        if (!subs.contains(e.getArgs())) {
            e.reply(invalid());
            return;
        }
        e.reply("Hollllup.....");
        MemePost post = reddit.getRandomMedia(e.getArgs());
        e.reply(new EmbedBuilder()
                .setAuthor("Meme from r/" + post.getSubreddit(), null, "https://www.redditstatic.com/desktop2x/img/favicon/android-icon-192x192.png")
                .setTitle(post.getTitle(), post.getPermaLink())
                .setDescription("Here is your random meme selected from r/" + post.getSubreddit())
                .setImage(post.getMediaUrl())
                .setFooter("Requested by: " + e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator(), e.getAuthor().getAvatarUrl())
                .build());
        cooldowns.put(e.getMember(), OffsetDateTime.now().plusMinutes(5));
    }

    private String createArgs() {
        StringBuilder sb = new StringBuilder();
        for (String str : subs)
            sb.append(str).append("/");
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    private int getTimeRemaining(Member member) {
        return (int) OffsetDateTime.now().until(cooldowns.get(member), ChronoUnit.SECONDS);
    }

}
