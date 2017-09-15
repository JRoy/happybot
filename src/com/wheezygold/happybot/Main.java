package com.wheezygold.happybot;

import com.jagrosh.jdautilities.commandclient.CommandClientBuilder;
import com.jagrosh.jdautilities.commandclient.examples.AboutCommand;
import com.wheezygold.happybot.Util.C;
import com.wheezygold.happybot.commands.*;
import com.wheezygold.happybot.events.AutoMod;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main extends ListenerAdapter {

    private static JDA jda;

    public static void main(String[] args) throws IOException, IllegalArgumentException, RateLimitedException, LoginException {
        C.log("Initializing the bot...");

        //Lets make the file so we can use it!
        File file = new File("config.yml");

        //We are going to check if the config exists, if not lets create one for them!
        if (!file.exists())
            file.createNewFile();

        //Allways init your strings!
        String token = null;
        //Create the file reader to get the first line.
        BufferedReader br = new BufferedReader(new FileReader("config.yml"));
        //Because null pointers.
        if (br.readLine() != null) {
            //We will get the token just in case, I don't know, maybe we want to log in.
            token = br.readLine();
        } else {
            //Let them know they are going to die.
            C.log("There is not token in your config, welcome to stack trace city!");
        }

//        EventWaiter waiter = new EventWaiter();

        new AutoMod("194473148161327104");
        //Start the AutoMod instance.

        C.log("Loading the command builder...");

        //Creates JDA-Util's Command Builder so we can use it later.
        CommandClientBuilder clientBuilder = new CommandClientBuilder();

        //Used for "ownerOnly" commands in commands.
        clientBuilder.setOwnerId("194473148161327104");

        //Used for the prefix of the bot, so we have an easy life.
        clientBuilder.setPrefix("^");

        C.log("Adding commands...");

        //Loads all of our commands into JDA-Util's command handler.
        clientBuilder.addCommands(

                new PingCommand(),
                new RulesCommand(),
//                new AboutCommand(Color.BLUE, "an example bot",
//                        new String[]{"Cool commands","Nice examples","Lots of fun!"},
//                        new Permission[]{Permission.ADMINISTRATOR}),
                new SpamCommand(),
                new FanartCommand(),
                new PromoteCommand(),
                new StaffManagementCommand(),
                new ShutdownCommand(),
                new EvalCommand());

        C.log("Constructing the JDA Instance...");

        //Start JDA Instance
        try {
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(token)
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    //Listens to the MessageReceivedEvent.
                    .addEventListener(clientBuilder.build())
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

        C.log("Bot has been loaded!");
    }

    @Override
    public void onShutdown(ShutdownEvent event) {
        C.log("The JDA instance has been shutdown!");
    }

    /**
     * @return Returns the JDA Instance
     */
    public static JDA getJda() {
        return jda;
    }

}
