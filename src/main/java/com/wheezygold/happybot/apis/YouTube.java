package com.wheezygold.happybot.apis;

import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.JSON;
import org.json.JSONObject;

import java.io.IOException;

/**
 * While is does use an API Call, I could not be bothered to make a full wrapper for the YouTube API (That retains the instance). This should be used as a token based system.
 */
public class YouTube {

    private String apiKey = "AIzaSyAPiPPTl1ZAsI1k_dCxHj7_RS7mfC-Dvuw";
    private JSONObject jsonResponse;

    public YouTube pullAPI() {
        try {
            //Take the API Key idc, it's got nothing but youtube shit.
            JSONObject fullResponse = JSON.readJsonFromUrl("https://www.googleapis.com/youtube/v3/channels?part=statistics&id=UC-enFKOrEf6N2Kq_YG3sFcQ&key=" + apiKey);
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
