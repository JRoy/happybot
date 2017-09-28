package com.wheezygold.happybot.events;

import com.wheezygold.happybot.Main;
import com.wheezygold.happybot.util.C;

import java.util.Timer;
import java.util.TimerTask;

public class UploadMonitor {

    private Timer timer;
    private final String apiKey;

    public UploadMonitor(String apiKey) {
        this.apiKey = apiKey;
        timer = new Timer();
        startTimer("df");
    }

    //https://www.googleapis.com/youtube/v3/search?key=AIzaSyBTinqKGviMXS3rYZmbjQOSuZQV_mB5hsM&channelId=UC-enFKOrEf6N2Kq_YG3sFcQ&part=snippet,id&order=date&maxResults=10
    public void startTimer(String chnid) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                C.getGuild().getTextChannelById("267145626469400586").sendMessage("Test").queue();
            }
        }, 0, 2*1000);
    }
}
