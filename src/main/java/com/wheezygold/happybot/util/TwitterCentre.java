package com.wheezygold.happybot.util;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterCentre {

    private static Twitter twitter;

    private static final long happyid = Long.parseLong("865017489213673472");

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

}
