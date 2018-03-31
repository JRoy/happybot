package io.github.jroy.happybot.apis.youtube;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import io.github.jroy.happybot.Main;
import io.github.jroy.happybot.apis.APIBase;
import io.github.jroy.happybot.util.Constants;
import io.github.jroy.happybot.util.Logger;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.StatusChangeEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * While is does use an API Call, I could not be bothered to make a full wrapper for the YouTube API (That retains the instance). This should be used as a token based system.
 */
public class YouTubeAPI extends APIBase {

    private final String apiKey;
    private final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private final JsonFactory JSON_FACTORY = new JacksonFactory();
    YouTube youTube;
    private List<ChannelBase> channels = new ArrayList<>();

    public YouTubeAPI(String apiKey) {
        super("YouTube");
        this.apiKey = apiKey;
    }

    @Override
    public void loadApi() {
        youTube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, request -> {}).setApplicationName("happybot").build();
        loadChannels();
        new Listener(channels.size());
        Logger.info("YouTubeAPI Connected to API, waiting to listen to upload events.");
    }

    private void loadChannels() {
        channels.add(new ChannelBase(Constants.HAPPYHEART_CHANNEL_ID.get(), this, true)); //Happy
        channels.add(new ChannelBase("UCBvPS7EWHVm0JSb8uYp4bNQ", this, true)); //Fetus
        channels.add(new ChannelBase("UC6c1n5yv8vdPAijuXR-pFzQ", this, false)); //Sime
        channels.add(new ChannelBase("UCzD9jOPren4Gi00wCv_NbXg", this, false)); //Wheezy
    }

    String getApiKey() {
        return apiKey;
    }

    private class Listener extends ListenerAdapter {

        private final int channelSize;

        Listener(int channelSize) {
            Main.registerEventListener(this);
            this.channelSize = channelSize;
        }

        @Override
        public void onStatusChange(StatusChangeEvent event) {
            if (event.getStatus() == JDA.Status.CONNECTED) {
                channels.forEach(ChannelBase::registerListener);
                Logger.info("YouTubeAPI is now listening to uploads from " + String.valueOf(channelSize) + " channels!");
            }
        }
    }

}
