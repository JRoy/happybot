package com.wheezygold.happybot.embed;

public enum EmbedIMG {

    INFO("https://cdn3.iconfinder.com/data/icons/glypho-generic-icons/64/info-circle-128.png"),
    CHECK("https://cdn1.iconfinder.com/data/icons/ui-5/502/check-64.png"),
    ERROR("https://cdn0.iconfinder.com/data/icons/social-messaging-ui-color-shapes/128/alert-circle-red-512.png");

    private String url;

    EmbedIMG(String url) {

        this.url = url;

    }

    public String geturl() {
        return url;
    }

}
