package com.wheezygold.happybot.events;

import com.wheezygold.happybot.Main;
import net.dv8tion.jda.core.EmbedBuilder;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.awt.*;

public class TweetMonitor {
    public TweetMonitor(String cKey, String cSecret, String aToken, String aSecret) {
        long happyid = Long.parseLong("865017489213673472");
        long wheezyid = Long.parseLong("3437978211");
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(cKey)
                .setOAuthConsumerSecret(cSecret)
                .setOAuthAccessToken(aToken)
                .setOAuthAccessTokenSecret(aSecret);
       TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
       StatusListener listener = new StatusListener() {
           @Override
           public void onStatus(Status status) {
               EmbedBuilder builder = new EmbedBuilder()
                       .setThumbnail(status.getUser().getBiggerProfileImageURL())
                       .setTitle("@" + status.getUser().getScreenName() + " has just tweeted", "https://twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId())
                       .setDescription(status.getText())
                       .setColor(new Color(0,172,237));
               if (status.getInReplyToScreenName() != null) {
                   builder.addField("In reply to..","[In reply to this @" + status.getInReplyToScreenName() + "'s tweet]" +
                           "(https://twitter.com/" + status.getInReplyToScreenName() + "/status/" + status.getInReplyToStatusId() + ")", false);
               }
               Main.getJda().getTextChannelById("362333614580432896").sendMessage(builder.build()).queue();
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
       filter.follow(happyid, wheezyid);
       twitterStream.filter(filter);
    }
}
