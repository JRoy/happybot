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
                "needed some help with french!",
                "wanted depression, but they don't even have to ask!",
                "Welcome to french class!",
                "needed depression, but they already got it.",
                "\"Where is happoheart?!!\"",
                "+5 Karma!",
                "welcome to toys r us we have free type two diabetes.",
                "hope you have brain.",
                "came to learn about interior bed defense.",
                "go watch Riverdale.",
                "came looking for technoblade.",
                "has made a mistake that can never be reversed!",
                "Well! Excuuuusee me, Princess!",
                "is probably :nose:-less!",
                "wants to use tnt instead of pvp.",
                "used Join! It's super effective.",
                "shut up marguerite!",
                "saw their teacher at the end of the hallway and hid here.",
                "has no clue what they're getting into.",
                "has joined!!?!?!?! (NOT CLICKBAIT) (GONE WRONG) (MUST SEE) (OMG)!",
                "what are the chances! Of human dums so far from Earth!",
                "actually read Happy's description!",
                "would like to learn how to rap bars.",
                "star this.",
                "came to fund the answer to depression.",
                "joined the man in the yellow hat official discord.",
                "sorry no technoblade here.",
                "joined. ffs. YOU TRY AND COME UP WITH THESES MESSAGES!",
                "wanted to be famous like happyheart!",
                "got out of the school hallway!",
                "came here to advertise.",
                "congratulations.",
                "came to see how it's like to be depressed.",
                "oh god... why.",
                "want depression? **welcome**.",
                "okay, have fun.",
                "grows way too many noses and joins this server",
                "^money admin give 999 <player>",
                "welco..**this welcoming has been interrupted by shameless promotion for happyheart so go ahead and donate to him at patreon.com/happyheart**",
                "You have a brain? You're welcome my dude!",
                "Welcome, <player>! Just in case you were wondering, no you're never gonna be able to talk to happyheart... (unless you donate to him on patreon.com/happyheart)",
                "HEY! Close the door, you'll let in the stupid!",
                "You're lucky <player>, there's like 82 different welcome messages and you managed to get this one, go win the lottery or something.",
                "This discord is not suitable for children or those who are easily disturbed.",
                "Wanted to post a welcome-submit!",
                "found out that your welcome submit has a better chance of being pinned if it has something to do with Josh in it",
                "Does anyone here have skype?",
                "You shouldn't have come here <player>, but we appreciate the support",
                "Came here to see Josh, not happyheart",
                "why?",
                "We should probably help them escape here now...",
                "If this is a good welcome submit, Josh will pin it and <player> joins with satisfaction",
                "is ban evading!!!",
                "has lost faith in the human race",
                "has chosen to become a 1 star"
        };
        goodbyemsgs = new String[]{
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
                "Alright then. Be that way.",
                "couldn't find their totinos.",
                "did the best they could by leaving.",
                "abused HYPIXEL YT RANK!!!!!!!!!!!",
                "does not have a happy heart.",
                "has gone to his moms purse to get some money for happy's patreon.",
                "was kicked for not giving Josh any Totinos Pizza Rolls.",
                "has deleted their livestream, along with their channel.",
                "spoiled Riverdale in Josh's DMs.",
                "went to CVS to buy more anti-depressants.",
                "is using high jump!",
                "turn off your aura. I'm live XD.",
                "tried to steal the Happy Bot.",
                "wasn’t excused by the princess.",
                "has passed french class.",
                "was banned for bringing Ducky back.",
                "with all of their power used at once, managed to escape the clutches of the deadly happyheart.",
                "hated the new update.",
                "had one too many bridges for breakfast.",
                "josh pls explain.",
                "went off to make a happyheart diss track.",
                "tried to left click, and their mind exploded as they did so.",
                "went to study French in a secret mountain lair.",
                "got beaned.",
                "left because their welcome submit didn't get pinned.",
                "was doing Hypixel minecraft high school role play.",
                "AonAiAi was banned for hacking. Thank you for reporting.",
                "burned because they were made of wood",
                "left since their application got denied by Josh.",
                "forgot to add spooky in their name.",
                "agrees with Tenebrous.",
                "sent hate to LegoMaestro.",
                "/hub",
                "yeah, grow a brain and then come back, you brain-less guy.",
                "got AonAiAi'd",
                "^money admin take 999999 <player>",
                "lost to net neutrality and had to pay for their websites",
                "gave up trying to be friends with Josh",
                "got scared. happyheart was coming for their bed",
                "GETLESSBADGETLESSBADGETLESSBADGETLESSBAD",
                "couldn't take all the depression",
                "because dental hygiene.",
                "comitted the worst crime... chatting in #welcome-submit",
                "Sorry, but no-brainers aren't allowed in here.",
                "And let the door hit you on the way out! Maybe it'll knock intelligence into you!",
                "prefers the old rickroll",
                "thought Gwen was an anticheat",
                "couldn't handle all the drama",
                "<player>'s bed was destroyed!",
                "saw Mr. Dants",
                "had good judgement after all",
                "was Mr. Dants in disguise!",
                "with the power of AonAiAi happyheart forbids you back to heck!",
                "realized no Siberia bullying!11!!!1",
                "Be gone thot!",
                "said \"GG\" but changed their mind",
                "yeah respect staff you loser.",
                "was triggered by Omicron having free will",
                "thought they aced happyheart's parkour quiz",
                "pinged Josh one too many times",
                "fanboyed over happyheart, but not Joshua",
                "couldn't handle Kaynta's gifs.",
                "-5 Karma!",
                "yeah heck off",
                "wasn't depressed enough",
                "chose technoblade over happyheart"

        };
    }

    /**
     * Displays the WelcomeMessage Stats
     *
     * @param chnl Channel to send the stats.
     */
    public static void showStats(String chnl) {
        C.getGuild().getTextChannelById(chnl).sendMessage(":information_source: Welcome Queue Stats:\n**Welcome Messages:** " + welcomemsgs.length + "\n**Quit Messages:** " + goodbyemsgs.length).queue();
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        String joinformat = welcomemsgs[random.nextInt(welcomemsgs.length)].replaceAll("<player>", event.getMember().getAsMention());
        if (!joinformat.startsWith("'s"))
            joinformat = " " + joinformat;
        event.getGuild().getTextChannelById("237363812842340363").sendMessage(event.getMember().getAsMention() + joinformat).queue();
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        String leaveformat = goodbyemsgs[random.nextInt(goodbyemsgs.length)].replaceAll("<player>", event.getMember().getAsMention());
        if (!leaveformat.startsWith("'s"))
            leaveformat = " " + leaveformat;
        event.getGuild().getTextChannelById("237363812842340363").sendMessage("**" + event.getMember().getUser().getName() + "**" + leaveformat).queue();
    }

}
