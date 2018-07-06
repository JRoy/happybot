package io.github.jroy.happybot.levels;

import net.dv8tion.jda.core.entities.Member;

public class LevelingToken {

    private final Member member;
    private long exp;
    private long level;


    LevelingToken(Member member, long exp, long level) {
        this.member = member;
        this.exp = exp;
        this.level = level;
    }

    public long getExp() {
        return exp;
    }

    public long getLevel() {
        return level;
    }

    public Member getMember() {
        return member;
    }
}