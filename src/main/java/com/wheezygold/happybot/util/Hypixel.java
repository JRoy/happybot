package com.wheezygold.happybot.util;

import com.kbrewster.hypixelapi.HypixelAPI;
import com.kbrewster.hypixelapi.exceptions.APIException;
import com.kbrewster.hypixelapi.player.Player;

import java.io.IOException;

public class Hypixel {

    private static HypixelAPI api;

    public Hypixel(String apikey) {
        api = new HypixelAPI(apikey);
    }

    public static Player getPlayer(String playerName) throws APIException {
        try {
            return api.getPlayer(playerName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getLevel(String playerName) throws APIException {
        try {
            return String.valueOf(api.getPlayer(playerName).getNetworkLevel());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMC(String playerName) throws APIException {
        try {
            return api.getPlayer(playerName).getMcVersionRp();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getRank(String playerName) throws APIException {
        try {
            return api.getPlayer(playerName).getRank();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
