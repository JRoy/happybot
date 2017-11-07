package com.wheezygold.happybot.events;

import com.wheezygold.happybot.util.Channels;
import com.wheezygold.happybot.util.Roles;
import com.wheezygold.happybot.util.TwitterCentre;
import net.dv8tion.jda.core.EmbedBuilder;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class TweetMonitor {
    public TweetMonitor(String cKey, String cSecret, String aToken, String aSecret) {
        long happyid = TwitterCentre.getHappyid();
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(false)
                .setOAuthConsumerKey(cKey)
                .setOAuthConsumerSecret(cSecret)
                .setOAuthAccessToken(aToken)
                .setOAuthAccessTokenSecret(aSecret);
        TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        StatusListener listener = new StatusListener() {
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
        };
        twitterStream.addListener(listener);
        FilterQuery filter = new FilterQuery();
        filter.follow(happyid);
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
            if (status.getUser().getId() == TwitterCentre.getHappyid()) {
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
