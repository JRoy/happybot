package com.wheezygold.happybot;

import com.jagrosh.jdautilities.commandclient.CommandClientBuilder;
import com.wheezygold.happybot.commands.*;
import com.wheezygold.happybot.events.*;
import com.wheezygold.happybot.theme.ThemeManager;
import com.wheezygold.happybot.util.*;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class Main extends ListenerAdapter {

    private Main instance = this;
    private static JDA jda;
    private static CommandClientBuilder clientBuilder;
    private static String theme;
    private static TwitterCentre twitterCentre;
    private static TweetMonitor tweetMonitor;
    private static Hypixel hypixel;
    private static ThemeManager themeManager;

    public static void main(String[] args) throws IOException, IllegalArgumentException, RateLimitedException, LoginException {

        new Logger();

        Logger.info("Initializing the bot...");

        Logger.log("Loading Config Files...");
        createConfigFiles();


        String token = readFirstLineOfFile("config.yml", "There is no token in your config, welcome to stack trace city!");
        theme = readFirstLineOfFile("theme.yml", "Error receiving theme");

        //Always init your strings! (Techno-coder: Wheezy, you are a sad, sad person)
        String sqlPassword = null;
        sqlPassword = readFirstLineOfFile("sql.yml", "Error receiving your SQL Password");

        themeManager = loadThemeManager();

        loadTweetMonitor();

        //Load our SQL Stuff
//        SQLManager sqlManager = new SQLManager("root", sqlpass);


        Logger.info("Loading the Hypixel API...");
        loadHypixelAPI();

        List<EventListener> eventListeners = loadEventListeners();
        loadClientBuilder();

        Logger.info("Constructing the JDA Instance...");
        JDABuilder builder = new JDABuilder(AccountType.BOT)
                .setToken(token)
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                //Listens to the MessageReceivedEvent.
                .addEventListener(clientBuilder.build())
                .setGame(Game.of("Loading"));
        for (EventListener listener : eventListeners)
            builder.addEventListener(listener);
        jda = builder.buildAsync();

//        new RichPresence((JDAImpl) jda);

        Logger.info("Bot has been loaded!");
    }

    private static ThemeManager loadThemeManager() {
        return new ThemeManager();
    }

    private static List<EventListener> loadEventListeners() {
        List<EventListener> eventListeners = new ArrayList<>();

        Logger.info("Loading AutoMod...");
        eventListeners.add(new AutoMod());

        Logger.info("Loading Welcome Manager...");
        eventListeners.add(new WelcomeMessage());

        Logger.info("Loading AutoReact...");
        eventListeners.add(new AutoReact());

        Logger.info("Loading Message Starer...");
        eventListeners.add(new StarMessages());

        return eventListeners;
    }

    private static void loadClientBuilder() {
        Logger.info("Loading the command builder...");

        //Creates JDA-Util's Command Builder so we can use it later.
        clientBuilder = new CommandClientBuilder();

        //Used for "ownerOnly" commands in commands.
        clientBuilder.setOwnerId(Constants.OWNER_ID.get());

        //Used for the prefix of the bot, so we have an easy life.
        clientBuilder.setPrefix("^");

        Logger.info("Adding commands...");

        //Loads all of our commands into JDA-Util's command handler.
        clientBuilder.addCommands(

                new PingCommand(),
                new RulesCommand(),
                new ApplyCommand(),
                new RewardsCommand(),
                new MathCommand(),
                new VideoCommand(),
                new SeasonCommand(),
                new RandomSeasonCommand(),
                new StatsCommand(hypixel),
                new HypixelCommand(hypixel),
                new WelcomeStatsCommand(),
                new ServersCommand(),
                new MentionCommand(),
                new SpamCommand(),
                new OgCommand(),
                new FansCommand(),
                new LockCommand(),
                new UnlockCommand(),
                new FanartCommand(),
                new KickCommand(),
                new BanCommand(),
                new PardonCommand(),
                new PromoteCommand(),
                new DemoteCommand(),
                new StaffManagementCommand(),
                new ThemeCommand(themeManager),
                new ThemeManagerCommand(themeManager),
                new ShutdownCommand(),
                new UpdateCommand(),
                new EvalCommand());

        clientBuilder.setHelpFunction(C::showHelp);
    }

    private static void loadHypixelAPI() throws IOException {
        String apiKey = null;
        BufferedReader hypixelReader = new BufferedReader(new FileReader("hypixel.yml"));
        try {
            apiKey = hypixelReader.readLine();
            hypixelReader.close();
        } catch (NullPointerException hex) {
            Logger.error("Error loading the Hypixel api-key: " + hex.getMessage());
        }
        hypixel = new Hypixel(apiKey);
    }

    private static void loadTweetMonitor() throws IOException {
        String cKey = null;
        String cSecret = null;
        String  aToken = null;
        String aSecret = null;
        BufferedReader twitterReader = new BufferedReader(new FileReader("twitter.yml"));
        try {
            cKey = twitterReader.readLine();
            cSecret = twitterReader.readLine();
            aToken = twitterReader.readLine();
            aSecret = twitterReader.readLine();
            twitterReader.close();
        } catch (NullPointerException tex) {
            Logger.error("Error loading the Twitter credentials: " + tex.getMessage());
        }

        Logger.info("Loading Twitter Centre...");
        twitterCentre = new TwitterCentre(cKey, cSecret, aToken, aSecret);
        Logger.info("Loading Twitter Monitor...");
        tweetMonitor = new TweetMonitor(cKey, cSecret, aToken, aSecret);
    }

    private static String readFirstLineOfFile(String filename, String errorMessage) throws IOException {
        BufferedReader configReader = new BufferedReader(new FileReader(filename));
        try {
            String line = configReader.readLine();
            configReader.close();
            return line;
        } catch (NullPointerException cex) {
            Logger.error(errorMessage);
        }
        return "";
    }

    private static void createConfigFiles() throws IOException {
        //Lets make the file so we can use it!
        File configfile = createFile("config.yml");
        File twitterfile = createFile("twitter.yml");
        File sqlfile = createFile("sql.yml");
        File themefile = new File("theme.yml");

        //Create theme file with defaults if it does not exist
        boolean themeFileNotExists = themefile.createNewFile();
        if (themeFileNotExists) {
            FileWriter fw = new FileWriter(themefile);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("normal");
            bw.close();
            fw.close();
        }
    }

    /**
     * Creates a new file if it does not exist
     *
     * @param filename The filename for the file
     * @return The reference to the open file
     * @throws IOException If the file could not be created
     */
    private static File createFile(String filename) throws IOException {
        File file = new File(filename);
        boolean doesNotExist = file.createNewFile();
        return file;
    }


    /**
     * An easy way to get our JDA Instance!
     *
     * @return Returns the JDA Instance.
     */
    public static JDA getJda() {
        return jda;
    }

    /**
     * An easy way to get our CommandClientBuilder instance!
     *
     * @return Returns the CommandClientBuilder Instance.
     */
    @SuppressWarnings("unused")
    public static CommandClientBuilder getClientBuilder() {
        return clientBuilder;
    }

    public static String getTheme() {
        return theme;
    }

    public static void updateTheme() {
        try {
            BufferedReader themereader = new BufferedReader(new FileReader("theme.yml"));
            //We will get the token just in case, I don't know, maybe we want to log in.
            theme = themereader.readLine();
            Logger.info("Theme Loaded: " + theme + "!");
            themereader.close();
        } catch (NullPointerException e) {
            //Let them know they are going to die.
            Logger.error("Error receiving your theme: " + e.getMessage());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Logger.error("Error receiving your theme: " + e.getMessage());
        }
    }

}
