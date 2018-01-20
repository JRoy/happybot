package com.wheezygold.happybot.events;

import com.wheezygold.happybot.util.Channels;
import com.wheezygold.happybot.util.Roles;
import com.wheezygold.happybot.apis.TwitterCentre;
import net.dv8tion.jda.core.EmbedBuilder;
import twitter4j.*;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class TweetMonitor {

    private final TwitterCentre twitterCentre;

    public TweetMonitor(TwitterCentre twitterCentre) {
        this.twitterCentre = twitterCentre;
        TwitterStream twitterStream = new TwitterStreamFactory(twitterCentre.getTwitter().getConfiguration()).getInstance();
        twitterStream.addListener(new StatusListener() {
            @Override
            public void onStatus(Status status) {
                new Thread(new HandleTweet(status)).start();
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

            }

            @Override
            public void onTrackLimitationNotice(int i) {

            }

            @Override
            public void onScrubGeo(long l, long l1) {

            }

            @Override
            public void onStallWarning(StallWarning stallWarning) {

            }

            @Override
            public void onException(Exception e) {

            }
        });
        FilterQuery filter = new FilterQuery();
        filter.follow(twitterCentre.getHappyid());
        twitterStream.filter(filter);
    }

    private class HandleTweet implements Runnable {

        private Status status;

        HandleTweet(Status status) {
            this.status = status;
        }

        @Override
        public void run() {
            Roles.TWITTER.getRole().getManager().setMentionable(true).queue();
            EmbedBuilder builder = new EmbedBuilder()
                    .setThumbnail(status.getUser().getBiggerProfileImageURL())
                    .setTitle("@" + status.getUser().getScreenName() + " has just tweeted", "https://twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId())
                    .setDescription(status.getText())
                    .setColor(new Color(0, 172, 237));
            if (status.getInReplyToScreenName() != null) {
                builder.addField("In reply to..", "[In reply to this @" + status.getInReplyToScreenName() + "'s tweet]" +
                        "(https://twitter.com/" + status.getInReplyToScreenName() + "/status/" + status.getInReplyToStatusId() + ")", false);
            }
            if (status.getUser().getId() == twitterCentre.getHappyid()) {
                Channels.TWITTER.getChannel().sendMessage(Roles.TWITTER.getRole().getAsMention()).queue();
                Channels.TWITTER.getChannel().sendMessage(builder.build()).queue();
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                Roles.TWITTER.getRole().getManager().setMentionable(false).queue();
            }
        }

    }

}
