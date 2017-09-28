package com.wheezygold.happybot;

import com.jagrosh.jdautilities.commandclient.CommandClientBuilder;
import com.wheezygold.happybot.events.UploadMonitor;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.commands.*;
import com.wheezygold.happybot.events.AutoMod;
import com.wheezygold.happybot.events.WelcomeMessage;
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
        File file = new File("config.yml");

        //We are going to check if the config exists, if not lets create one for them!
        if (!file.exists())
            file.createNewFile();

        //Always init your strings!
        String token = null;
        //Create the file reader to get the first line.
        BufferedReader br = new BufferedReader(new FileReader("config.yml"));
        //Because null pointers.
        try {
            //We will get the token just in case, I don't know, maybe we want to log in.
            token = br.readLine();
            C.log("Token has been acquired!");
        } catch (NullPointerException ex1) {
            //Let them know they are going to die.
            C.log("There is not token in your config, welcome to stack trace city!");
        }

        //Start the AutoMod instance.
        C.log("Loading AutoMod...");
        AutoMod autoMod = new AutoMod("194473148161327104");

        //Start the WelcomeMessage instance.
        C.log("Loading Welcome Manager...");
        WelcomeMessage welcomeMessage = new WelcomeMessage();

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
                new VideoCommand(),
//                new AboutCommand(Color.BLUE, "an example bot",
//                        new String[]{"Cool commands","Nice examples","Lots of fun!"},
//                        new Permission[]{Permission.ADMINISTRATOR}),
                new SpamCommand(),
                new FanartCommand(),
                new BanCommand(),
                new PardonCommand(),
                new PromoteCommand(),
                new StaffManagementCommand(),
                new ShutdownCommand(),
                new UpdateCommand(),
                new EvalCommand());

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

        C.log("Initializing the console...");
        //Lets me run java code in the console directly!
        new Console();

        C.log("Loading Upload Monitor");
        new UploadMonitor("d");

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
    public static CommandClientBuilder getClientBuilder() { return clientBuilder; }

}
