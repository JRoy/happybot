package io.github.jroy.happybot.util;

import lombok.Data;

@Data
public class BotConfig {
  private final String botToken;
  private final String hypixelApiKey;
  private final String riotApiKey;
  private final String sqlPassword;
  private final String prefix;
  private final String alternativePrefix;

  private final String twitterOauthKey;
  private final String twitterOauthSecret;
  private final String twitterAccessToken;
  private final String twitterAccessTokenSecret;
}
