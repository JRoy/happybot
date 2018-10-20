package io.github.jroy.happybot.apis.youtube;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import io.github.jroy.happybot.apis.APIBase;
import io.github.jroy.happybot.util.Logger;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class YouTubeAPI extends APIBase {
  public static final String HAPPYHEART_YOUTUBE_ID = "UC-enFKOrEf6N2Kq_YG3sFcQ";
  public static final String FETUS_YOUTUBE_ID = "UCBvPS7EWHVm0JSb8uYp4bNQ";
  public static final String WHEEZY_YOUTUBE_ID = "UCzD9jOPren4Gi00wCv_NbXg";

  @Getter(AccessLevel.PROTECTED)
  private final String apiKey;
  private final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
  private final JsonFactory JSON_FACTORY = new JacksonFactory();
  protected YouTube youTube;
  private List<ChannelBase> channels = new ArrayList<>();

  public YouTubeAPI(String apiKey) {
    super("YouTube");
    this.apiKey = apiKey;
  }

  @Override
  public void loadApi() {
    youTube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, null)
        .setApplicationName("happybot").build();
    loadChannels();
    Logger.info("YouTubeAPI Connected to API, waiting to listen to upload events.");
  }

  @Override
  public void onJdaLogin() {
    channels.forEach(ChannelBase::registerListener);
    Logger.info("YouTubeAPI is now listening to uploads from " + channels.size() + " channels!");
  }

  private void loadChannels() {
    channels.add(new ChannelBase(HAPPYHEART_YOUTUBE_ID, this, true));
    channels.add(new ChannelBase(FETUS_YOUTUBE_ID, this, true));
    channels.add(new ChannelBase(WHEEZY_YOUTUBE_ID, this, false));
  }
}
