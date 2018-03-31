package io.github.jroy.happybot;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import io.github.jroy.happybot.apis.APIBase;
import io.github.jroy.happybot.apis.Hypixel;
import io.github.jroy.happybot.apis.League;
import io.github.jroy.happybot.apis.TwitterCentre;
import io.github.jroy.happybot.apis.exceptions.IllegalAPIState;
import io.github.jroy.happybot.apis.youtube.YouTubeAPI;
import io.github.jroy.happybot.commands.*;
import io.github.jroy.happybot.commands.money.GambleCommand;
import io.github.jroy.happybot.commands.money.MoneyCommand;
import io.github.jroy.happybot.commands.money.ShopCommand;
import io.github.jroy.happybot.commands.report.EditReportCommand;
import io.github.jroy.happybot.commands.report.HandleReportCommand;
import io.github.jroy.happybot.commands.report.LookupReportCommand;
import io.github.jroy.happybot.commands.report.ReportCommand;
import io.github.jroy.happybot.commands.warn.*;
import io.github.jroy.happybot.events.*;
import io.github.jroy.happybot.sql.ReportManager;
import io.github.jroy.happybot.sql.SQLManager;
import io.github.jroy.happybot.sql.WarningManager;
import io.github.jroy.happybot.theme.ThemeManager;
import io.github.jroy.happybot.util.Constants;
import io.github.jroy.happybot.util.Logger;
import io.github.jroy.happybot.util.MessageFactory;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class Main extends ListenerAdapter {

    private Main instance = this;
    private static JDA jda;
    private static CommandClientBuilder clientBuilder;
    private static String theme;
    private static TwitterCentre twitterCentre;
    private static TweetMonitor tweetMonitor;
    private static SQLManager sqlManager;
    private static WarningManager warningManager;
    private static ReportManager reportManager;
    private static Hypixel hypixel;
    private static ThemeManager themeManager;
    private static MessageFactory messageFactory;
    private static League league;
    private static List<EventListener> eventListeners = new ArrayList<>();

    public static void main(String[] args) throws IOException, IllegalArgumentException, LoginException {

        new Logger();

        Logger.info("Initializing happybot...");

        Logger.log("Loading Config Files...");
        createConfigFiles();

        loadApis();

        String token = readFirstLineOfFile("config.yml", "There is no token in your config, welcome to stack trace city!");
        theme = readFirstLineOfFile("theme.yml", "Error receiving theme");

        String sqlPassword;
        sqlPassword = readFirstLineOfFile("sql.yml", "Error receiving your SQL Password");

        themeManager = loadThemeManager();
        messageFactory = loadMessageFactory();

        loadTweetMonitor();

        //Load our SQL Stuff
        sqlManager = new SQLManager(sqlPassword);

        Logger.info("Loading Warning Manager...");
        warningManager = new WarningManager(sqlManager);

        Logger.info("Loading Report Manager...");
        reportManager = new ReportManager(sqlManager);

        List<EventListener> eventListeners = loadEventListeners();
        loadClientBuilder();

        Logger.info("Constructing the JDA Instance...");
        JDABuilder builder = new JDABuilder(AccountType.BOT)
                .setToken(token)
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                //Listens to the MessageReceivedEvent.
                .addEventListener(clientBuilder.build())
                .setGame(Game.of(Game.GameType.DEFAULT, "Loading"));
        for (EventListener listener : eventListeners)
            builder.addEventListener(listener);
        Logger.info("Logging into Discord...");
        jda = builder.buildAsync();

//        new RichPresence((JDAImpl) jda);

        Logger.info("Bot has been loaded & Connected to Discord!");
    }

    private static void loadApis() throws IOException {
        Logger.info("Initializing APIs...");
        List<APIBase> apis = new ArrayList<>();
        apis.add(hypixel = new Hypixel(readFirstLineOfFile("hypixel.yml", "Error receiving hypixel api key.")));
        String cKey;
        String cSecret;
        String aToken;
        String aSecret;
        BufferedReader twitterReader = new BufferedReader(new FileReader("twitter.yml"));
        cKey = twitterReader.readLine();
        cSecret = twitterReader.readLine();
        aToken = twitterReader.readLine();
        aSecret = twitterReader.readLine();
        twitterReader.close();
        apis.add(twitterCentre = new TwitterCentre(cKey, cSecret, aToken, aSecret));
        apis.add(league = new League(readFirstLineOfFile("lol.yml", "Error receiving lol api key.")));
        apis.add(new YouTubeAPI("AIzaSyCR_UuC2zxDJ8KxbFElFrCVdN4uY739HAE")); //API Key is restricted to the VM this bot runs on, don't waist your time...
        Logger.info("Logging into APIs...");
        for (APIBase api : apis) {
            try {
                api.loginApi();
            } catch (IllegalAPIState illegalAPIState) {
                Logger.error("Could not not load an API: " + illegalAPIState.getMessage());
            }
        }
        Logger.info("Loaded APIS:");
        for (APIBase api : apis) {
            Logger.info("- " + api.getApiName());
        }
    }

    public static void registerEventListener(EventListener eventListener) {
        eventListeners.add(eventListener);
    }

    private static MessageFactory loadMessageFactory() { return new MessageFactory(); }

    private static ThemeManager loadThemeManager() {
        return new ThemeManager();
    }

    private static List<EventListener> loadEventListeners() {

        Logger.info("Loading AutoMod...");
        eventListeners.add(new AutoMod(messageFactory));

        Logger.info("Loading Welcome Manager...");
        eventListeners.add(new WelcomeMessage(messageFactory));

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

        clientBuilder.setHelpConsumer(e -> {
            e.replySuccess("Help is on the way! :sparkles:");
            StringBuilder builder = new StringBuilder("**"+e.getSelfUser().getName()+"** commands:\n");
            Command.Category category = null;
            for(Command command : e.getClient().getCommands())
            {
                if(!command.isHidden() && (!command.isOwnerCommand() || e.isOwner()))
                {
                    if(!Objects.equals(category, command.getCategory()))
                    {
                        category = command.getCategory();
                        builder.append("\n\n  __").append(category==null ? "No Category" : category.getName()).append("__:\n");
                    }
                    builder.append("\n`").append(e.getClient().getPrefix()).append((e.getClient().getPrefix()==null?" ":"")).append(command.getName())
                            .append(command.getArguments()==null ? "`" : " "+command.getArguments()+"`")
                            .append(" - ").append(command.getHelp());
                }
            }
            User owner = e.getJDA().getUserById(e.getClient().getOwnerId());
            if(owner!=null)
            {
                builder.append("\n\nFor additional help, contact **").append(owner.getName()).append("**#").append(owner.getDiscriminator());
                if(e.getClient().getServerInvite()!=null)
                    builder.append(" or join ").append(e.getClient().getServerInvite());
            }
            if(e.isFromType(ChannelType.TEXT))
                e.reactSuccess();
            e.replyInDm(builder.toString(), unused -> {}, t -> e.replyWarning("Help cannot be sent because you are blocking Direct Messages."));
        });

        //Loads all of our commands into JDA-Util's command handler.
        clientBuilder.addCommands(

                //General
                new PingCommand(),
                new RulesCommand(),
                new ApplyCommand(),
                new RewardsCommand(),
                new MentionCommand(),
                new ServersCommand(),
                new SeasonCommand(),
                new SelfWarningsCommand(warningManager),
                new ReportCommand(reportManager),

                //Fun

                new HecktownCommand(),
                new MathCommand(),
                new VideoCommand(),
                new RandomSeasonCommand(),
                new StatsCommand(hypixel, league),
                new HypixelCommand(hypixel),
                new LeagueCommand(league),
                new MessageStatsCommand(messageFactory),
                new MoneyCommand(sqlManager),
                new GambleCommand(sqlManager),
                new ShopCommand(sqlManager),

                //Staff Tools

                new WarnCommand(warningManager, messageFactory),
                new EditWarningCommand(warningManager),
                new DeleteWarnCommand(warningManager),
                new WarningsCommand(warningManager),
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
                new HandleReportCommand(reportManager),
                new LookupReportCommand(reportManager),
                new EditReportCommand(reportManager),
                new PurgeCommand(),

                //Bot Management

                new RuntimeCommand(),
                new ThemeCommand(themeManager),
                new ThemeManagerCommand(themeManager),
                new ShutdownCommand(),
                new UpdateCommand(messageFactory),
                new EvalCommand());
    }

    private static void loadTweetMonitor() {
        Logger.info("Loading Twitter Monitor...");
        tweetMonitor = new TweetMonitor(twitterCentre);
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
        File leaguefile = createFile("lol.yml");

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
            BufferedReader themeReader = new BufferedReader(new FileReader("theme.yml"));
            theme = themeReader.readLine();
            Logger.info("Theme Loaded: " + theme + "!");
            themeReader.close();
        } catch (NullPointerException e) {
            Logger.error("Error receiving your theme: " + e.getMessage());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Logger.error("Error receiving your theme: " + e.getMessage());
        }
    }

}
