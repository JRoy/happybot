package io.github.jroy.happybot.apis.reddit;

import com.google.gson.JsonObject;

public class MemePost {

    private String title;
    private String subreddit;
    private String permaLink;
    private String mediaUrl;

    MemePost(JsonObject dataObject) {
        title = dataObject.get("title").getAsString();
        subreddit = dataObject.get("subreddit").getAsString();
        permaLink = "https://reddit.com" + dataObject.get("permalink").getAsString();
        mediaUrl = dataObject.getAsJsonObject("preview").getAsJsonArray("images").get(0).getAsJsonObject().getAsJsonObject("source").get("url").getAsString();
    }

    public String getTitle() {
        return title;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public String getPermaLink() {
        return permaLink;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }
}
