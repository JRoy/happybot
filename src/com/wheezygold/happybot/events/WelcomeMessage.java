package com.wheezygold.happybot.events;

import com.wheezygold.happybot.util.C;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Random;

public class WelcomeMessage extends ListenerAdapter {
    private static String[] welcomemsgs;
    private static String[] goodbyemsgs;
    private final Random random;

    /**
     * Creates an WelcomeMessage Instance!
     */
    public WelcomeMessage() {
        this.random = new Random();
        welcomemsgs = new String[]{
                "Welcome to the happyheart fandom! You are being targeted because you are a famous YouTuber!1!",
                "my man, you have entered the realm of severe depression.",
                "you have entered the realm of the totino gods, do you have anything to say?",
                "Welcome to the happyheart discord. You must be crazy for joining.",
                "Welcome to happyheart's fanbase!.. you are ugly.",
                "has entered hell :)",
                "is possibly mentally retarted cause he came here...",
                "has had a bad case of idiocity cause he is here.",
                "gain a nosewave.",
                "get to work get busy!",
                "get my money!",
                "MOM get the cam.",
                "hi and welcome to our restaurant, u smell sorry no pies for u.",
                "welcome to the realm of confusion.",
                "crap another one joined...",
                "joining this discord wont cure your depression.",
                "oh great, someone else threw their trash here!",
                "just got pranked and joined the discord.",
                "pressed the join button by accident.",
                "has found our secret totino stash ( ͡ಠ ʖ̯ ͡ಠ)!",
                "dropped his diamonds off the edge and went after them.",
                "got baited by the giveaway!",
                "wanted to know where to get the shears hacked client.",
                "has entered the valley of depression.",
                "is a proud Mineplex hero!",
                "want depression?",
                "smelled the totinos in the microwave.",
                "tried to jitter bridge, but failed and ended up here.",
                "wanted to find happyheart's subbot.",
                "here's the new wave degeneracy disorder.",
                "just joined...ACT LIKE YOU'RE BUSY!",
                "is here to sell their kidney in exchange for MVP+.",
                "was told Dotz is so eaeaeaeaeaeseey so he joined.",
                "used the wrong discord link.",
                "I hope you enjoy your stay because there's no way back!",
                "no no no this is a bad thing no no no this is a bad thing.",
                "french class gave them chronic heart attacks.",
                "hi, Josh made me write this message. this was happyheart's message, though. feel blessed.",
                "thanks for watching!",
                "wanted depression and didn't even have to ask!",
                "has joined the depression support circle.",
                "got cat fished by happyheart and was told to come here to be his egirl.",
                "welcome to isle stupid. Try not to run in the the idiots.",
                "you just advanced to depression level 0.",
                "needed some help with french!"

        };
        goodbyemsgs = new String[] {
                "just left happyheart Fanbase. You smel.",
                "come back pls ur not ugly.",
                "left, kthxbai.",
                "left, who made this guy get triggered and leave?",
                "left, maybe the totino gods didnt like him?",
                "lose a nosewave.",
                "get pranked as heck!1!",
                "get gameplayed!",
                "you got outplayed QUICKLY.",
                "Wake up you're useless!",
                "u still smell go shower ur bad.",
                "leaving this discord wont cure your depression.",
                "wasn't trash enough for this discord.",
                "was so useless that they left the server.",
                "kicked due to unfair skydiving technique",
                "failed the triple neo.",
                "became intelligent and left for their own sanity!",
                "skydived out of the server.",
                "gave up on the french class.",
                "was sent to french class.",
                "got pranked as heck.",
                "was a spy for AonAiAi!",
                "couldn't handle the subscriber count!",
                "went to hackusate happyheart live.",
                "left to watch Rebel_Guy",
                "timed out.",
                "couldn't figure out the quadruple neo.",
                "was kicked for bed nuker.",
                "only watched quality gameplay...",
                "went to heck",
                "couldn't handle french class.",
                "has been killed by a hypixel tryhard! **FINAL KILL**!",
                "quit because his welcome-submit didn't get submitted.",
                "ran out of bridges for breakfast!",
                "refused to take french class.",
                "quickly sprinted to the kitchen to get some more tontinos.",
                "good choice.",
                "Banned by Watchdog (Wait, who am I kidding).",
                "AonAiAi does not approve.",
                "git gud and skydive with me.",
                "was bad at pvp so they used TNT!",
                "crashed because KillAura = Less FPS!",
                "accidentally ate End Stone thinking it was Frosted Mini Wheats and died.",
                "e.",
                "you could leave if you want, but once you see this server, you can never un-see it.",
                "are you even able to read your own leave message?",
                "thanks for watching part 2",
                "had enough depression for one day!",
                "Au revoir!",
                "Alright then. Be that way."

        };
    }

    /**
     * Displays the WelcomeMessage Stats
     * @param chnl Channel to send the stats.
     */
    public static void showStats(String chnl) {
        C.getGuild().getTextChannelById(chnl).sendMessage(":information_source: Welcome Queue Stats:\n**Welcome Messages:** " + welcomemsgs.length + "\n**Quit Messages:** " + goodbyemsgs.length).queue();
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        event.getGuild().getTextChannelById("237363812842340363").sendMessage(event.getMember().getAsMention() + " " + welcomemsgs[random.nextInt(welcomemsgs.length)]).queue();
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        event.getGuild().getTextChannelById("237363812842340363").sendMessage("**" + event.getMember().getUser().getName() + "** " + goodbyemsgs[random.nextInt(goodbyemsgs.length)]).queue();
    }

}
