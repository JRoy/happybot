package io.github.jroy.happybot;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import io.github.jroy.happybot.apis.*;
import io.github.jroy.happybot.apis.exceptions.IllegalAPIState;
import io.github.jroy.happybot.apis.reddit.Reddit;
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
import io.github.jroy.happybot.events.AutoMod;
import io.github.jroy.happybot.events.StarMessages;
import io.github.jroy.happybot.events.SubmitPinner;
import io.github.jroy.happybot.events.WelcomeMessage;
import io.github.jroy.happybot.sql.ReportManager;
import io.github.jroy.happybot.sql.SQLManager;
import io.github.jroy.happybot.sql.timed.EventManager;
import io.github.jroy.happybot.sql.WarningManager;
import io.github.jroy.happybot.theme.DiscordThemerImpl;
import io.github.jroy.happybot.util.BotConfig;
import io.github.jroy.happybot.util.Constants;
import io.github.jroy.happybot.util.Logger;
import io.github.jroy.happybot.sql.MessageFactory;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class Main extends ListenerAdapter {

    private Main instance = this;
    private static YamlFile yamlFile;
    private static BotConfig botConfig;
    private static JDA jda;
    private static CommandClientBuilder clientBuilder;
    private static TwitterCentre twitterCentre;
    private static SQLManager sqlManager;
    private static WarningManager warningManager;
    private static ReportManager reportManager;
    private static Hypixel hypixel;
    private static DiscordThemerImpl themeManager;
    private static MessageFactory messageFactory;
    private static EventManager eventManager;
    private static League league;
    private static Reddit reddit;
    private static List<EventListener> eventListeners = new ArrayList<>();

    public static void main(String[] args) throws IOException, IllegalArgumentException, LoginException {

        new Logger();

        System.setProperty("http.agent", "happybot");

        Logger.info("Initializing happybot...");

        Logger.log("Loading Config Files...");

        yamlFile = new YamlFile("settings.yml");

        if (!yamlFile.exists()) {
            yamlFile.createNewFile(true);
        }
        try {
            Logger.info("Reading Config...");
            yamlFile.load();
            loadConfig();
        } catch (InvalidConfigurationException e) {
            Logger.error("Un-parsable Settings!");
            e.printStackTrace();
        }

        loadApis();

        themeManager = loadThemeManager();

        //Load our SQL Stuff
        sqlManager = new SQLManager(botConfig.getSqlPassword());

        messageFactory = loadMessageFactory();

        Logger.info("Loading Warning Manager...");
        warningManager = new WarningManager(sqlManager);

        Logger.info("Loading Report Manager...");
        reportManager = new ReportManager(sqlManager);

        List<EventListener> eventListeners = loadEventListeners();
        loadClientBuilder();

        Logger.info("Constructing JDA Instance...");
        JDABuilder builder = new JDABuilder(AccountType.BOT)
                .setToken(botConfig.getBotToken())
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

    private static void loadConfig() throws IOException, InvalidConfigurationException {
        //Defaults...
        if (!yamlFile.isSet("token"))
            yamlFile.set("token", "");
        if (!yamlFile.isSet("hypixel-api-key"))
            yamlFile.set("hypixel-api-key", "");
        if (!yamlFile.isSet("riot-api-key"))
            yamlFile.set("riot-api-key", "");
        if (!yamlFile.isSet("sql-password"))
            yamlFile.set("sql-password", "");
        if (!yamlFile.isSet("reddit.username"))
            yamlFile.set("reddit.username", "");
        if (!yamlFile.isSet("reddit.password"))
            yamlFile.set("reddit.password", "");
        if (!yamlFile.isSet("reddit.client-id"))
            yamlFile.set("reddit.client-id", "");
        if (!yamlFile.isSet("reddit.client-secret"))
            yamlFile.set("reddit.client-secret", "");
        if (!yamlFile.isSet("twitter.oauth-key"))
            yamlFile.set("twitter.oauth-key", "");
        if (!yamlFile.isSet("twitter.oauth-secret"))
            yamlFile.set("twitter.oauth-secret", "");
        if (!yamlFile.isSet("twitter.access-token"))
            yamlFile.set("twitter.access-token", "");
        if (!yamlFile.isSet("twitter.access-token-secret"))
            yamlFile.set("twitter.access-token-secret", "");
        yamlFile.save();
        yamlFile.load();

        String token = yamlFile.getString("token");
        String hypixel = yamlFile.getString("hypixel-api-key");
        String riot = yamlFile.getString("riot-api-key");
        String sql = yamlFile.getString("sql-password");
        String redditUsername = yamlFile.getString("reddit.username");
        String redditPassword = yamlFile.getString("reddit.password");
        String redditId = yamlFile.getString("reddit.client-id");
        String redditSecret = yamlFile.getString("reddit.client-secret");
        String twitterOKey = yamlFile.getString("twitter.oauth-key");
        String twitterOSecret = yamlFile.getString("twitter.oauth-secret");
        String twitterAToken = yamlFile.getString("twitter.access-token");
        String twitterASecret = yamlFile.getString("twitter.access-token-secret");

        botConfig = new BotConfig(token, hypixel, riot, sql, redditUsername, redditPassword, redditId, redditSecret, twitterOKey, twitterOSecret, twitterAToken, twitterASecret);
        Logger.info("Loaded Config!");
    }

    private static void loadApis() {
        Logger.info("Initializing APIs...");
        List<APIBase> apis = new ArrayList<>();
        apis.add(reddit = new Reddit(botConfig));
        apis.add(hypixel = new Hypixel(botConfig.getHypixelApiKey()));
        apis.add(twitterCentre = new TwitterCentre(botConfig.getTwitterOauthKey(), botConfig.getTwitterOauthSecret(), botConfig.getTwitterAccessToken(), botConfig.getTwitterAccessTokenSecret()));
        apis.add(league = new League(botConfig.getRiotApiKey()));
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

    private static MessageFactory loadMessageFactory() { return new MessageFactory(sqlManager); }

    private static DiscordThemerImpl loadThemeManager() {
        DiscordThemerImpl themer = new DiscordThemerImpl(true);
        eventListeners.add(themer);
        return themer;
    }

    private static List<EventListener> loadEventListeners() {

        Logger.info("Loading AutoMod...");
        eventListeners.add(new AutoMod(messageFactory));

        Logger.info("Loading Welcome Manager...");
        eventListeners.add(new WelcomeMessage(messageFactory));

        Logger.info("Loading Message Starer...");
        eventListeners.add(new StarMessages());

        Logger.info("Loading Event Manager...");
        eventListeners.add(eventManager = new EventManager(sqlManager));

        eventListeners.add(new SubmitPinner());

        return eventListeners;
    }

    private static void loadClientBuilder() {
        Logger.info("Loading Command Builder...");

        //Creates JDA-Util's Command Builder so we can use it later.
        clientBuilder = new CommandClientBuilder();

        //Used for "ownerOnly" commands in commands.
        clientBuilder.setOwnerId(Constants.OWNER_ID.get());

        //Used for the prefix of the bot, so we have an easy life.
        clientBuilder.setPrefix("^");

        Logger.info("Adding commands...");

        //Spaghetti Code!
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
                new MemeCommand(reddit),

                //Staff Tools

                new WarnCommand(warningManager, messageFactory),
                new EditWarningCommand(warningManager),
                new DeleteWarnCommand(warningManager),
                new WarningsCommand(warningManager),
                new SpamCommand(eventManager),
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
                new MuteCommand(eventManager),
                new UnMuteCommand(eventManager),
                new MessageFactoryCommand(messageFactory),
                new EmoteCommand(),

                //Bot Management

                new RuntimeCommand(),
                new ThemeCommand(themeManager),
//                new ThemeManagerCommand(themeManager),
                new ShutdownCommand(),
                new UpdateCommand(messageFactory),
                new EvalCommand());
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

}
