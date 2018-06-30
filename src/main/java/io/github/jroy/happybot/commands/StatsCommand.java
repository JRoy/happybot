package io.github.jroy.happybot.commands;

import com.kbrewster.exceptions.APIException;
import com.kbrewster.hypixelapi.player.HypixelPlayer;
import io.github.jroy.happybot.apis.Hypixel;
import io.github.jroy.happybot.apis.League;
import io.github.jroy.happybot.apis.youtube.YouTubeRealTime;
import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.C;
import net.dv8tion.jda.core.EmbedBuilder;
import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.constant.Platform;

import java.util.HashMap;

public class StatsCommand extends CommandBase {

    private League league;
    private Hypixel hypixel;

    public StatsCommand(Hypixel hypixel, League league) {
        super("stats", "<youtube/hypixel/league>", "Gives stats of a happyheart's channel and hypixel player.", CommandCategory.FUN);
        this.hypixel = hypixel;
        this.league = league;
    }

    @Override
    protected void executeCommand(CommandEvent e) {
        if (e.getArgs().equalsIgnoreCase("youtube")) {
            new Thread(new GetYoutubeStats(e)).start();
        } else if (e.getArgs().equalsIgnoreCase("hypixel")) {
            new Thread(new GetHypixelStats(e)).start();
        } else if (e.getArgs().equalsIgnoreCase("league")) {
            new Thread(new GetLoLStats(e)).start();
        } else {
            e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
        }
    }

    class GetLoLStats implements Runnable {

        private CommandEvent e;

        GetLoLStats(CommandEvent e) {
            this.e = e;
        }

        @Override
        public void run() {

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("LoL Player Statistics")
                    .setDescription("Listing statistics:")
                    .setFooter("Stats provided by Riot Games's API", "http://i.imgur.com/xNLs83T.png");
            try {
                for (HashMap.Entry<String, String> entry : league.getAllFields("happyheart", Platform.NA).entrySet()) {
                    if (entry.getValue() != null && !entry.getValue().equals("0")) {
                        embed.addField("**" + entry.getKey() + "**", entry.getValue(), true);
                    }
                }
            } catch (RiotApiException e1) {
                e1.printStackTrace();
            }
            e.reply(embed.build());
        }
    }

    class GetYoutubeStats implements Runnable {

        private CommandEvent e;

        GetYoutubeStats(CommandEvent e) {
            this.e = e;
        }

        @Override
        public void run() {

            YouTubeRealTime youTube = new YouTubeRealTime().pullAPI();

            e.reply(new EmbedBuilder()
                    .setTitle("Happyheart's YouTube Statistics")
                    .setDescription("Listing Statistics:")
                    .setFooter("Stats provided by YouTube's Realtime API", "http://www.stickpng.com/assets/images/580b57fcd9996e24bc43c545.png")
                    .addField("**Subscribers**", youTube.fetchSubs(), true)
                    .addField("**Videos**", youTube.fetchVids(), true)
                    .addField("**Views**", youTube.fetchViews(), true)
                    .build());
            youTube.finish();
        }
    }

    class GetHypixelStats implements Runnable {

        private CommandEvent e;

        GetHypixelStats(CommandEvent e) {
            this.e = e;
        }

        @Override
        public void run() {
            HypixelPlayer hypixelPlayer = null;
            try {
                hypixelPlayer = hypixel.getPlayer("happyheart");
            } catch (APIException e1) {
                e1.printStackTrace();
            }

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Happyheart's Hypixel Statistics")
                    .setDescription("Listing statistics:")
                    .setFooter("Stats provided by Hypixel's API", "https://media-curse.cursecdn.com/attachments/264/727/f7c76fdb4569546a9ddf0e58c8653823.png");

            for (HashMap.Entry<String, String> entry : hypixel.getAllFields(hypixelPlayer).entrySet()) {
                if (entry.getValue() != null) {
                    embed.addField("**" + entry.getKey() + "**", entry.getValue(), true);
                }
            }

            e.reply(embed.build());
        }
    }

}
