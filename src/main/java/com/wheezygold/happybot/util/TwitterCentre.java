package com.wheezygold.happybot.util;

import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterCentre {

    public static Twitter twitter;

    public static final long happyid = Long.parseLong("865017489213673472");

    public TwitterCentre(String cKey, String cSecret, String aToken, String aSecret) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(false)
                .setOAuthConsumerKey(cKey)
                .setOAuthConsumerSecret(cSecret)
                .setOAuthAccessToken(aToken)
                .setOAuthAccessTokenSecret(aSecret);
        TwitterFactory twitterFactory = new TwitterFactory(cb.build());
        twitter = twitterFactory.getInstance();
    }

    public static long getHappyid() {
        return happyid;
    }

    public static int getFollowers() {
        try {
            return twitter.getFollowersIDs(happyid, Long.parseLong("-1")).getIDs().length;
        } catch (TwitterException e) {
            return 0;
        }
    }

    public static int getLikes() {
        try {
            return twitter.getFavorites(happyid, new Paging(1, 2000)).size();
        } catch (TwitterException e) {
            return 0;
        }
    }

    public static int getTweets() {
        try {
            return twitter.getUserTimeline(happyid, new Paging(1, 2000)).size();
        } catch (TwitterException e) {
            return 0;
        }
    }

}
