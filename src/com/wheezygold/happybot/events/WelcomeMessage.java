package com.wheezygold.happybot.events;

import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Random;

public class WelcomeMessage extends ListenerAdapter {
    private final String[] welcomemsgs;
    private final String[] goodbyemsgs;

    /**
     * Creates an WelcomeMessage Instance!
     */
    public WelcomeMessage() {
        this.welcomemsgs = new String[]{
                "Welcome to the happyheart fandom! You are being targeted because you are a famous YouTuber!1!",
                "my man, you have entered the realm of severe depression",
                "you have entered the realm of the totino gods, do you have anything to say?",
                "Welcome to the happyheart discord. You must be crazy for joining.",
                "Welcome to happyheart's fanbase!.. you are ugly",
                "has entered hell :)",
                "is possibly mentally retarted cause he came here...",
                "has had a bad case of idiocity cause he is here",
        };
        this.goodbyemsgs = new String[] {
                "just left happyheart Fanbase. You smel.",
                "come back pls ur not ugly",
                "left, kthxbai",
                "left, who made this guy get triggered and leave?",
                "left, maybe the totino gods didnt like him?",
        };
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Random random = new Random();
        int index = random.nextInt(welcomemsgs.length);
        event.getGuild().getTextChannelById("237363812842340363").sendMessage(event.getMember().getAsMention() + " " + welcomemsgs[index]).queue();
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        Random random = new Random();
        int index = random.nextInt(goodbyemsgs.length);
        event.getGuild().getTextChannelById("237363812842340363").sendMessage(event.getMember().getUser().getName() + " " + goodbyemsgs[index]).queue();
    }

}
