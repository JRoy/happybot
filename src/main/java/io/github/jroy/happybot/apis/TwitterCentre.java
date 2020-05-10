package io.github.jroy.happybot.apis;

import io.github.jroy.happybot.util.Channels;
import io.github.jroy.happybot.util.Constants;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.api.EmbedBuilder;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class TwitterCentre extends APIBase {

  private static final long HAPPY_ID = Long.parseLong(Constants.HAPPYHEART_TWITTER_ID.get());
  private final String cKey;
  private final String cSecret;
  private final String aToken;
  private final String aSecret;

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
    Twitter twitter = twitterFactory.getInstance();
    TwitterStream twitterStream = new TwitterStreamFactory(twitter.getConfiguration()).getInstance();
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
    filter.follow(HAPPY_ID);
    twitterStream.filter(filter);
  }

  private static class HandleTweet implements Runnable {

    private final Status status;

    HandleTweet(Status status) {
      this.status = status;
    }

    @Override
    public void run() {
      Roles.TWITTER.getRole().getManager().setMentionable(true).complete();
      EmbedBuilder builder = new EmbedBuilder()
          .setThumbnail(status.getUser().getBiggerProfileImageURL())
          .setTitle("@" + status.getUser().getScreenName() + " has just tweeted", "https://twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId())
          .setDescription(status.getText())
          .setColor(new Color(0, 172, 237));
      if (status.getInReplyToScreenName() != null) {
        builder.addField("In reply to..", "[In reply to this @" + status.getInReplyToScreenName() + "'s tweet]" +
            "(https://twitter.com/" + status.getInReplyToScreenName() + "/status/" + status.getInReplyToStatusId() + ")", false);
      }
      if (status.getUser().getId() == HAPPY_ID) {
        Channels.TWITTER.getChannel().sendMessage(Roles.TWITTER.getRole().getAsMention()).complete();
        Channels.TWITTER.getChannel().sendMessage(builder.build()).complete();
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
