package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.JSON;
import com.wheezygold.happybot.util.YouTube;
import net.dv8tion.jda.core.EmbedBuilder;
import org.json.JSONObject;

import java.io.IOException;

public class StatsCommand extends Command {

    public StatsCommand() {
        this.name = "stats";
        this.help = "Gives stats of a happy's channel.";
        this.category = new Category("Fun");
    }

    @Override
    protected void execute(CommandEvent e) {
        new Thread(new GetStats(e)).start();
    }

    class GetStats implements Runnable {

        private CommandEvent e;

        public GetStats(CommandEvent e) {
            this.e = e;
        }

        @Override
        public void run() {

            YouTube youTube = new YouTube().pullAPI();

            e.reply(new EmbedBuilder()
                    .setTitle("**Happyheart's Statistics**")
                    .setDescription("Listing statistics:")
                    .setFooter("Stats provided by YouTube's Realtime API", "https://www.youtube.com/yts/img/favicon-vfl8qSV2F.ico")
                    .addField("**Subscribers**", youTube.fetchSubs(), true)
                    .addField("**Videos**", youTube.fetchVids(), true)
                    .addField("**Views**", youTube.fetchViews(), true)
                    .build());
            youTube.finish();
        }
    }

}
