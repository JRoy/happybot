package io.github.jroy.happybot.apis.reddit;

import com.google.gson.JsonObject;
import lombok.Getter;

@Getter
public class MemePost {
  private final String title;
  private final String subreddit;
  private final String permaLink;
  private String mediaUrl;

  private final boolean selfPost;
  private final String selfText;

  private final boolean isNsfw;

  MemePost(JsonObject dataObject) {
    title = dataObject.get("title").getAsString();
    subreddit = dataObject.get("subreddit").getAsString();
    permaLink = "https://reddit.com" + dataObject.get("permalink").getAsString();

    selfPost = dataObject.get("is_self").getAsBoolean();
    selfText = dataObject.get("selftext").getAsString();

    if (!selfPost) {
      mediaUrl = dataObject.get("url").getAsString();
    }

    isNsfw = dataObject.get("over_18").getAsBoolean();
  }
}
