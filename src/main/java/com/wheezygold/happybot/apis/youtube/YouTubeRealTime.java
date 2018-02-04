package com.wheezygold.happybot.apis.youtube;

import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Constants;
import com.wheezygold.happybot.util.JSON;
import org.json.JSONObject;

import java.io.IOException;

@SuppressWarnings("FieldCanBeLocal")
public class YouTubeRealTime {

    /**
     * Key is restricted to the VM this lives on. (Nice try)
     */
    private String apiKey = "AIzaSyCR_UuC2zxDJ8KxbFElFrCVdN4uY739HAE";
    private JSONObject jsonResponse;

    public YouTubeRealTime pullAPI() {
        try {
            JSONObject fullResponse = JSON.readJsonFromUrl("https://www.googleapis.com/youtube/v3/channels?part=statistics&id=" + Constants.HAPPYHEART_CHANNEL_ID.get() +"&key=" + apiKey);
            jsonResponse = JSON.readFromText(JSON.readFromText(fullResponse.getJSONArray("items").get(0).toString()).get("statistics").toString());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return this;
    }

    public String fetchSubs() {
        return C.prettyNum(Integer.parseInt(jsonResponse.getString("subscriberCount")));
    }

    public String fetchVids() {
        return C.prettyNum(Integer.parseInt(jsonResponse.getString("videoCount")));
    }

    public String fetchViews() {
        return C.prettyNum(Integer.parseInt(jsonResponse.getString("viewCount")));
    }

    public void finish() {
        jsonResponse = null;
    }

}
