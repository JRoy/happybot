package com.wheezygold.happybot.apis;

import com.wheezygold.happybot.util.Constants;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterCentre extends APIBase {

    private Twitter twitter;
    private final String cKey;
    private final String cSecret;
    private final String aToken;
    private final String aSecret;

    private static final long happyid = Long.parseLong(Constants.HAPPYHEART_TWITTER_ID.get());

    public TwitterCentre(String cKey, String cSecret, String aToken, String aSecret) {
        super("Twitter");
        this.cKey = cKey;
        this.cSecret = cSecret;
        this.aToken = aToken;
        this.aSecret = aSecret;
    }

    @Override
    public void loadApi() {
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
