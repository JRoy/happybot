package com.wheezygold.happybot.sql;

public class UserToken {

    private Long id;
    private String userid;
    private int coins;
//    private long epoch;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

//    public long getEpoch() {
//        return epoch;
//    }
//
//    public void setEpoch(long epoch) {
//        this.epoch = epoch;
//    }


}
