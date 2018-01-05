package com.wheezygold.happybot.util;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterCentre {

    private final Twitter twitter;

    private static final long happyid = Long.parseLong(Constants.HAPPYHEART_TWITTER_ID.get());

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

    public long getHappyid() {
        return happyid;
    }

    public Twitter getTwitter() {
        return twitter;
    }
}
