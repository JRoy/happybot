package com.wheezygold.happybot.util;

import net.dv8tion.jda.core.WebSocketCode;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import org.json.JSONObject;

public class RichPresence {

    public RichPresence(JDAImpl jda) { //JDA object can be casted to a JDAImpl
        JSONObject obj = new JSONObject();
        JSONObject gameObj = new JSONObject();

        /* LAYOUT:
        * name
        * details
        * time elapsed (timestamps)
        * status
        */
        gameObj.put("name", "Name Here");
        gameObj.put("type", 0); //1 if streaming
        gameObj.put("details", "Details Here");
        gameObj.put("state", "Spotify - PLAYING");
        gameObj.put("timestamps", new JSONObject().put("start", 1508373056)); //somehow used for the time elapsed thing I assume, you can probably also set the end to make it show "xx:xx left"

        JSONObject assetsObj = new JSONObject();
        assetsObj.put("large_image", "376410582829498368"); //ID of large icon
        assetsObj.put("large_text", "Large Text");

        assetsObj.put("small_image", "376410693861244928"); //ID of small icon

        gameObj.put("assets", assetsObj);
        gameObj.put("application_id", "354736186516045835"); //Application ID

        obj.put("game", gameObj);
        obj.put("afk", jda.getPresence().isIdle());
        obj.put("status", jda.getPresence().getStatus().getKey());
        obj.put("since", System.currentTimeMillis());

        System.out.println(obj);

        jda.getClient().send(new JSONObject()
                .put("d", obj)
                .put("op", WebSocketCode.PRESENCE).toString());
    }
}
