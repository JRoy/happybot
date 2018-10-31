package io.github.jroy.happybot.apis.youtube;

import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Channels;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@RequiredArgsConstructor
class ChannelBase {
  private final String channelId;
  private final YouTubeAPI youTubeAPI;
  private final boolean pingsEveryone;
  private boolean registered = false;
  private long currentVideo = -1;

  protected void registerListener() {
    if (registered) {
      return;
    }
    new Timer().scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        try {
          List<SearchResult> searchResults = youTubeAPI.youTube.search()
              .list("id,snippet")
              .setKey(youTubeAPI.getApiKey())
              .setChannelId(channelId)
              .setOrder("date")
              .setMaxResults(10L)
              .setType("video")
              .execute()
              .getItems();
          if (currentVideo < 0) {
            currentVideo = searchResults.get(0).getSnippet().getPublishedAt().getValue();
          }
          for (SearchResult result : searchResults) {
            SearchResultSnippet snippet = result.getSnippet();
            if (Long.compare(snippet.getPublishedAt().getValue(), currentVideo) <= 0) {
              continue;
            }
            sendAlert(result.getId().getVideoId(), snippet);
            currentVideo = snippet.getPublishedAt().getValue();
            break;
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }, 0, 120000);
    registered = true;
  }

  private void sendAlert(String vidId, SearchResultSnippet video) {
    StringBuilder builder = new StringBuilder();
    if (pingsEveryone) {
      builder.append("@everyone\n");
    }
    builder.append(C.bold(video.getChannelTitle() + " has uploaded a video!\n"));
    builder.append("https://youtu.be/").append(vidId);
    Channels.LIVE.getChannel().sendMessage(builder.toString()).queue();
  }

}
