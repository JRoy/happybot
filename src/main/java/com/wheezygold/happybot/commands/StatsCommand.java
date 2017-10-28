package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.JSON;
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
            JSONObject jsonObject = null;
            try {
                //Take the API Key idc, it's got nothing but youtube shit.
                jsonObject = JSON.readJsonFromUrl("https://www.googleapis.com/youtube/v3/channels?part=statistics&id=UC-enFKOrEf6N2Kq_YG3sFcQ&key=AIzaSyAPiPPTl1ZAsI1k_dCxHj7_RS7mfC-Dvuw");
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            JSONObject stats = JSON.readFromText(JSON.readFromText(jsonObject.getJSONArray("items").get(0).toString()).get("statistics").toString());

            String subs = C.prettyNum(Integer.parseInt(stats.getString("subscriberCount")));
            String vids = C.prettyNum(Integer.parseInt(stats.getString("videoCount")));
            String views = C.prettyNum(Integer.parseInt(stats.getString("viewCount")));

            e.reply(new EmbedBuilder()
                    .setTitle("**Happyheart's Statistics**")
                    .setDescription("Listing statistics:")
                    .setFooter("Stats provided by YouTube's Realtime API", "https://www.youtube.com/yts/img/favicon-vfl8qSV2F.ico")
                    .addField("**Subscribers**", subs, true)
                    .addField("**Videos**", vids, true)
                    .addField("**Views**", views, true)
                    .build());
        }
    }

}
