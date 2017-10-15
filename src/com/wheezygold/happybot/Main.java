package com.wheezygold.happybot;

import com.jagrosh.jdautilities.commandclient.CommandClientBuilder;
import com.wheezygold.happybot.commands.*;
import com.wheezygold.happybot.events.AutoMod;
import com.wheezygold.happybot.events.AutoReact;
import com.wheezygold.happybot.events.TweetMonitor;
import com.wheezygold.happybot.events.WelcomeMessage;
import com.wheezygold.happybot.util.C;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main extends ListenerAdapter {

    private static JDA jda;
    private static CommandClientBuilder clientBuilder;

    public static void main(String[] args) throws IOException, IllegalArgumentException, RateLimitedException, LoginException {
        C.log("Initializing the bot...");

        //Lets make the file so we can use it!
        File configfile = new File("config.yml");
        File twitterfile = new File("twitter.yml");

        //We are going to check if the config exists, if not lets create one for them!
        if (!configfile.exists())
            configfile.createNewFile();

        //Check Twitter Creds File, Create it if not a thing.
        if (!twitterfile.exists())
            twitterfile.createNewFile();

        //Always init your strings!
        String token = null;
        String cKey = null;
        String cSecret = null;
        String aToken = null;
        String aSecret = null;

        //config.yml Reader - Simpler, as we only need one line.

        //Create the file reader to get the first line.
        BufferedReader configreader = new BufferedReader(new FileReader("config.yml"));
        //Because null pointers.
        try {
            //We will get the token just in case, I don't know, maybe we want to log in.
            token = configreader.readLine();
            C.log("Token has been acquired!");
        } catch (NullPointerException cex) {
            //Let them know they are going to die.
            C.log("There is not token in your config, welcome to stack trace city!");
        }

        //twitter.yml Reader - Getting 4 lines, see how it works out.

        //Create our BufferedReader
        BufferedReader twitterreader = new BufferedReader(new FileReader("twitter.yml"));
        //Catch null pointers so we can tell if the config reader
        try {
            cKey = twitterreader.readLine();
            cSecret = twitterreader.readLine();
            aToken = twitterreader.readLine();
            aSecret = twitterreader.readLine();
            C.log("All Twitter credentials has been acquired.");
        } catch (NullPointerException tex) {
            C.log("Error loading the Twitter credentials: " + tex.getMessage());
        }

        //Start the TweetMonitor
        C.log("Loading Twitter Monitor...");
        new TweetMonitor(cKey, cSecret, aToken, aSecret);

        //Start the AutoMod instance.
        C.log("Loading AutoMod...");
        AutoMod autoMod = new AutoMod("194473148161327104");

        //Start the WelcomeMessage instance.
        C.log("Loading Welcome Manager...");
        WelcomeMessage welcomeMessage = new WelcomeMessage();

        //Start the AutoReact Instance.
        C.log("Loading AutoReact...");
        AutoReact autoReact = new AutoReact();

        C.log("Loading the command builder...");

        //Creates JDA-Util's Command Builder so we can use it later.
        clientBuilder = new CommandClientBuilder();

        //Used for "ownerOnly" commands in commands.
        clientBuilder.setOwnerId("194473148161327104");

        //Used for the prefix of the bot, so we have an easy life.
        clientBuilder.setPrefix("^");

        C.log("Adding commands...");

        //Loads all of our commands into JDA-Util's command handler.
        clientBuilder.addCommands(

                new PingCommand(),
                new RulesCommand(),
                new ApplyCommand(),
                new VideoCommand(),
                new SeasonCommand(),
                new RandomSeasonCommand(),
                new WelcomeStatsCommand(),
                new ServersCommand(),
                new MentionCommand(),
                new SpamCommand(),
                new OgCommand(),
                new LockCommand(),
                new UnlockCommand(),
                new FanartCommand(),
                new KickCommand(),
                new BanCommand(),
                new PardonCommand(),
                new PromoteCommand(),
                new DemoteCommand(),
                new StaffManagementCommand(),
                new ShutdownCommand(),
                new UpdateCommand(),
                new EvalCommand());

        clientBuilder.setHelpFunction(event -> C.showHelp(event));

        C.log("Constructing the JDA Instance...");

        //Start JDA Instance
        try {
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(token)
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    //Listens to the MessageReceivedEvent.
                    .addEventListener(clientBuilder.build())
                    .addEventListener(welcomeMessage)
                    .addEventListener(autoMod)
                    .addEventListener(autoReact)
                    //Because people gonna spam...
                    .useSharding(0, 2)
                    .setGame(Game.of("Loading"))
                    //No idea what the difference is...
                    .buildBlocking();
        } catch (InterruptedException e) {
            C.log("Error while logging into JDA Instance!");
            C.log("#-#-#-#-#-#-#-#-# Starting Stack Trace #-#-#-#-#-#-#-#-#");
            e.printStackTrace();
            C.log("#-#-#-#-#-#-#-#-# Ending Stack Trace #-#-#-#-#-#-#-#-#");
        }

//        JDAImpl jdaimpl = (JDAImpl) jda;
//
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("afk", false);
//        jsonObject.put("status", "dnd");
//        jsonObject.put("since", System.currentTimeMillis());
//
//        JSONObject gameObj = new JSONObject();
//        gameObj.put("name", "InteliJ");
//        gameObj.put("type", 0);
//        gameObj.put("state", "Developing");
//        gameObj.put("details", "Using JDA to make myself!");
//        gameObj.put("application_id", "354736186516045835");
//
//        JSONObject assetsObj = new JSONObject();
//        assetsObj.put("large_image", "358402218577231875");
//        assetsObj.put("small_image", "358402218577231875");
//        assetsObj.put("large_text", "Large Text");
//        assetsObj.put("small_text", "Small Text");
//
//        gameObj.put("assets", assetsObj);
//
//        jsonObject.put("game", gameObj);
//
//        jdaimpl.getClient().send(new JSONObject().put("d", jsonObject).put("op", WebSocketCode.PRESENCE).toString());

//        C.log("Loading Upload Monitor");
//        new UploadMonitor("d");

        C.log("Bot has been loaded!");
    }

    @Override
    public void onShutdown(ShutdownEvent event) {
        C.log("The JDA instance has been shutdown!");
    }

    /**
     * An easy way to get our JDA Instance!
     * @return Returns the JDA Instance.
     */
    public static JDA getJda() {
        return jda;
    }

    /**
     * An easy way to get our CommandClientBuilder instance!
     * @return Returns the CommandClientBuilder Instance.
     */
    @SuppressWarnings("unused")
    public static CommandClientBuilder getClientBuilder() { return clientBuilder; }

}
