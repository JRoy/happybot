package io.github.jroy.happybot.util;

public class BotConfig {

    private final String botToken;
    private final String hypixelApiKey;
    private final String riotApiKey;
    private final String sqlPassword;

    private final String redditUsername;
    private final String redditPassword;
    private final String redditClientId;
    private final String redditClientSecret;

    private final String twitterOauthKey;
    private final String twitterOauthSecret;
    private final String twitterAccessToken;
    private final String twitterAccessTokenSecret;

    public BotConfig(String botToken, String hypixelApiKey, String riotApiKey, String sqlPassword, String redditUsername, String redditPassword, String redditClientId, String redditClientSecret, String twitterOauthKey, String twitterOauthSecret, String twitterAccessToken, String twitterAccessTokenSecret) {
        this.botToken = botToken;
        this.hypixelApiKey = hypixelApiKey;
        this.riotApiKey = riotApiKey;
        this.sqlPassword = sqlPassword;

        this.redditUsername = redditUsername;
        this.redditPassword = redditPassword;
        this.redditClientId = redditClientId;
        this.redditClientSecret = redditClientSecret;

        this.twitterOauthKey = twitterOauthKey;
        this.twitterOauthSecret = twitterOauthSecret;
        this.twitterAccessToken = twitterAccessToken;
        this.twitterAccessTokenSecret = twitterAccessTokenSecret;
    }

    public String getBotToken() {
        return botToken;
    }

    public String getHypixelApiKey() {
        return hypixelApiKey;
    }

    public String getRiotApiKey() {
        return riotApiKey;
    }

    public String getSqlPassword() {
        return sqlPassword;
    }

    public String getRedditUsername() {
        return redditUsername;
    }

    public String getRedditPassword() {
        return redditPassword;
    }

    public String getRedditClientId() {
        return redditClientId;
    }

    public String getRedditClientSecret() {
        return redditClientSecret;
    }

    public String getTwitterOauthKey() {
        return twitterOauthKey;
    }

    public String getTwitterOauthSecret() {
        return twitterOauthSecret;
    }

    public String getTwitterAccessToken() {
        return twitterAccessToken;
    }

    public String getTwitterAccessTokenSecret() {
        return twitterAccessTokenSecret;
    }
}
