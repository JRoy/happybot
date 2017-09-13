package com.wheezygold.happybot;

import com.jagrosh.jdautilities.commandclient.CommandClientBuilder;
import com.jagrosh.jdautilities.commandclient.examples.AboutCommand;
import com.wheezygold.happybot.Util.C;
import com.wheezygold.happybot.commands.*;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main extends ListenerAdapter {

    private static JDA jda;

    public static void main(String[] args) throws IOException, IllegalArgumentException, RateLimitedException, LoginException {
        C.log("Initializing the bot...");

        String token = null;
        BufferedReader brTest = new BufferedReader(new FileReader("config.yml"));
        token = brTest.readLine();

        String ownerId = "194473148161327104";

//        EventWaiter waiter = new EventWaiter();

        C.log("Loading the command builder...");

        CommandClientBuilder clientBuilder = new CommandClientBuilder();

        clientBuilder.setOwnerId(ownerId);

        clientBuilder.setPrefix("^");

        C.log("Adding commands...");

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

        try {
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(token)
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .addEventListener(clientBuilder.build())
                    .useSharding(0, 2)
                    .setGame(Game.of("Loading"))
                    .buildBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        C.log("Initializing the console...");
        new Console();

        C.log("Bot has been loaded!");
    }

    @Override
    public void onShutdown(ShutdownEvent event) {
        C.log("The JDA instance has been shutdown!");
    }

    public static JDA getJda() {
        return jda;
    }

}
