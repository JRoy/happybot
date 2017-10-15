package com.wheezygold.happybot.util;

import net.dv8tion.jda.core.entities.Emote;

public enum Emotes {

    SPINNER("354100265852600322"),
    SADHEART("359429261762887680"),
    RESOURCE_PACK("354789422140489729"),
    HAPPYWEEB("354789048683855872"),
    HAPPYTRASH("357913402745552897"),
    HAPPYLAUGH("367354969495699456"),
    HAPPYL("368148164097736721"),
    HAPPYIRL("318023107333849098"),
    HAPPYGASM("354405601641496577"),
    HAPPYBOI("368147929753452544"),
    HAPPY_THONK("350737034928979968"),
    HACKCLIENT("355071947920375808"),
    GITGUD("354974389986394122"),
    BOBROSSHEART("366278593187282954");

    private String emote;

    Emotes(String emote) {
        this.emote = emote;
    }

    public String getId() {
        return emote;
    }

    public Emote getEmote() {
        return C.getGuild().getEmoteById(emote);
    }

    public static Emotes getRandom() {
        return values()[(int) (Math.random() * values().length)];
    }

}
