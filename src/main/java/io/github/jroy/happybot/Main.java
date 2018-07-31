package io.github.jroy.happybot;

import io.github.jroy.happybot.apis.APIBase;
import io.github.jroy.happybot.apis.Hypixel;
import io.github.jroy.happybot.apis.League;
import io.github.jroy.happybot.apis.TwitterCentre;
import io.github.jroy.happybot.apis.exceptions.IllegalAPIState;
import io.github.jroy.happybot.apis.reddit.Reddit;
import io.github.jroy.happybot.apis.youtube.YouTubeAPI;
import io.github.jroy.happybot.commands.ApplyCommand;
import io.github.jroy.happybot.commands.AvatarCommand;
import io.github.jroy.happybot.commands.BanCommand;
import io.github.jroy.happybot.commands.DemoteCommand;
import io.github.jroy.happybot.commands.DiceCommand;
import io.github.jroy.happybot.commands.EmoteCommand;
import io.github.jroy.happybot.commands.EvalCommand;
import io.github.jroy.happybot.commands.FactCommand;
import io.github.jroy.happybot.commands.FanartCommand;
import io.github.jroy.happybot.commands.FansCommand;
import io.github.jroy.happybot.commands.HecktownCommand;
import io.github.jroy.happybot.commands.HelpCommand;
import io.github.jroy.happybot.commands.HypixelCommand;
import io.github.jroy.happybot.commands.KickCommand;
import io.github.jroy.happybot.commands.LeagueCommand;
import io.github.jroy.happybot.commands.LockCommand;
import io.github.jroy.happybot.commands.MathCommand;
import io.github.jroy.happybot.commands.MemberCountCommand;
import io.github.jroy.happybot.commands.MemeCommand;
import io.github.jroy.happybot.commands.MentionCommand;
import io.github.jroy.happybot.commands.MessageFactoryCommand;
import io.github.jroy.happybot.commands.MessageStatsCommand;
import io.github.jroy.happybot.commands.MuteCommand;
import io.github.jroy.happybot.commands.OgCommand;
import io.github.jroy.happybot.commands.PardonCommand;
import io.github.jroy.happybot.commands.PingCommand;
import io.github.jroy.happybot.commands.PromoteCommand;
import io.github.jroy.happybot.commands.PurgeCommand;
import io.github.jroy.happybot.commands.RandomSeasonCommand;
import io.github.jroy.happybot.commands.RewardsCommand;
import io.github.jroy.happybot.commands.RulesCommand;
import io.github.jroy.happybot.commands.RuntimeCommand;
import io.github.jroy.happybot.commands.SeasonCommand;
import io.github.jroy.happybot.commands.SelfStarCommands;
import io.github.jroy.happybot.commands.ShippingCommand;
import io.github.jroy.happybot.commands.ShutdownCommand;
import io.github.jroy.happybot.commands.SpamCommand;
import io.github.jroy.happybot.commands.StaffManagementCommand;
import io.github.jroy.happybot.commands.StatsCommand;
import io.github.jroy.happybot.commands.TestCommand;
import io.github.jroy.happybot.commands.ThemeCommand;
import io.github.jroy.happybot.commands.UnlockCommand;
import io.github.jroy.happybot.commands.UpdateCommand;
import io.github.jroy.happybot.commands.VideoCommand;
import io.github.jroy.happybot.commands.WhoIsCommand;
import io.github.jroy.happybot.commands.base.CommandFactory;
import io.github.jroy.happybot.commands.levels.AddUserCommand;
import io.github.jroy.happybot.commands.levels.LeaderboardCommand;
import io.github.jroy.happybot.commands.levels.LevelCommand;
import io.github.jroy.happybot.commands.money.GambleCommand;
import io.github.jroy.happybot.commands.money.MoneyCommand;
import io.github.jroy.happybot.commands.money.ReclaimCommand;
import io.github.jroy.happybot.commands.money.RobCommand;
import io.github.jroy.happybot.commands.money.ShopCommand;
import io.github.jroy.happybot.commands.remind.DeleteRemindCommand;
import io.github.jroy.happybot.commands.remind.EditRemindCommand;
import io.github.jroy.happybot.commands.remind.RemindCommand;
import io.github.jroy.happybot.commands.remind.RemindersCommand;
import io.github.jroy.happybot.commands.report.EditReportCommand;
import io.github.jroy.happybot.commands.report.HandleReportCommand;
import io.github.jroy.happybot.commands.report.LookupReportCommand;
import io.github.jroy.happybot.commands.report.ReportCommand;
import io.github.jroy.happybot.commands.warn.DeleteWarnCommand;
import io.github.jroy.happybot.commands.warn.EditWarningCommand;
import io.github.jroy.happybot.commands.warn.SelfWarningsCommand;
import io.github.jroy.happybot.commands.warn.WarnCommand;
import io.github.jroy.happybot.commands.warn.WarningsCommand;
import io.github.jroy.happybot.events.AutoMod;
import io.github.jroy.happybot.events.LoggingFactory;
import io.github.jroy.happybot.events.SubmitPinner;
import io.github.jroy.happybot.events.WelcomeMessage;
import io.github.jroy.happybot.events.star.StarMessages;
import io.github.jroy.happybot.levels.Leveling;
import io.github.jroy.happybot.sql.MessageFactory;
import io.github.jroy.happybot.sql.PurchaseManager;
import io.github.jroy.happybot.sql.ReportManager;
import io.github.jroy.happybot.sql.SQLManager;
import io.github.jroy.happybot.sql.WarningManager;
import io.github.jroy.happybot.sql.timed.EventManager;
import io.github.jroy.happybot.theme.DiscordThemerImpl;
import io.github.jroy.happybot.util.BotConfig;
import io.github.jroy.happybot.util.Logger;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class Main extends ListenerAdapter {

  private static YamlFile yamlFile;
  private static BotConfig botConfig;
  private static JDA jda;
  private static CommandFactory commandFactory;
  private static TwitterCentre twitterCentre;
  private static SQLManager sqlManager;
  private static PurchaseManager purchaseManager;
  private static WarningManager warningManager;
  private static ReportManager reportManager;
  private static Hypixel hypixel;
  private static DiscordThemerImpl themeManager;
  private static MessageFactory messageFactory;
  private static EventManager eventManager;
  private static League league;
  private static Reddit reddit;
  private static StarMessages starMessages;
  private static Leveling leveling;
  private static List<EventListener> eventListeners = new ArrayList<>();
  private Main instance = this;

  public static void main(String[] args) throws IOException, IllegalArgumentException, LoginException, InterruptedException {

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
      System.exit(1);
    }

    loadApis();

    themeManager = loadThemeManager();

    //Load our SQL Stuff
    sqlManager = new SQLManager(botConfig.getSqlPassword());

    purchaseManager = new PurchaseManager(sqlManager);

    messageFactory = loadMessageFactory();

    Logger.info("Loading Warning Manager...");
    warningManager = new WarningManager(sqlManager);

    Logger.info("Loading Report Manager...");
    reportManager = new ReportManager(sqlManager);

    List<EventListener> eventListeners = loadEventListeners();
    loadCommandFactory();

    Logger.info("Constructing JDA Instance...");
    JDABuilder builder = new JDABuilder(AccountType.BOT)
        .setToken(botConfig.getBotToken())
        .setStatus(OnlineStatus.DO_NOT_DISTURB)
        //Listens to the MessageReceivedEvent.
        .addEventListener(commandFactory.build())
        .setGame(Game.of(Game.GameType.DEFAULT, "Loading"));
    for (EventListener listener : eventListeners) {
      builder.addEventListener(listener);
    }
    Logger.info("Logging into Discord...");
    jda = builder.buildBlocking();

    new LoggingFactory();

//        new RichPresence((JDAImpl) jda);

    Logger.info("Bot has been loaded & Connected to Discord!");
  }

  private static void loadConfig() throws IOException, InvalidConfigurationException {
    yamlFile.save();
    yamlFile.load();

    String token = getAndSet(yamlFile, "token");
    String hypixel = getAndSet(yamlFile, "hypixel-api-key");
    String riot = getAndSet(yamlFile, "riot-api-key");
    String sql = getAndSet(yamlFile, "sql-password");
    String redditUsername = getAndSet(yamlFile, "reddit.username");
    String redditPassword = getAndSet(yamlFile, "reddit.password");
    String redditId = getAndSet(yamlFile, "reddit.client-id");
    String redditSecret = getAndSet(yamlFile, "reddit.client-secret");
    String twitterOKey = getAndSet(yamlFile, "twitter.oauth-key");
    String twitterOSecret = getAndSet(yamlFile, "twitter.oauth-secret");
    String twitterAToken = getAndSet(yamlFile, "twitter.access-token");
    String twitterASecret = getAndSet(yamlFile, "twitter.access-token-secret");

    yamlFile.save();
    yamlFile.load();

    botConfig = new BotConfig(token, hypixel, riot, sql, redditUsername, redditPassword, redditId, redditSecret, twitterOKey, twitterOSecret, twitterAToken, twitterASecret);
    Logger.info("Loaded Config!");
  }

  /**
   * Gets the given path in the file.
   * If it does not exist, set it to the empty string and return that instead.
   */
  private static String getAndSet(YamlFile file, String path) {
    if (!file.isSet(path)) {
      file.set(path, "");
    }
    return file.getString(path);
  }

  private static void loadApis() {
    Logger.info("Initializing APIs...");
    List<APIBase> apis = new ArrayList<>();
    apis.add(reddit = new Reddit(botConfig));
    apis.add(hypixel = new Hypixel(botConfig.getHypixelApiKey()));
    apis.add(twitterCentre = new TwitterCentre(botConfig.getTwitterOauthKey(), botConfig.getTwitterOauthSecret(), botConfig.getTwitterAccessToken(), botConfig.getTwitterAccessTokenSecret()));
    apis.add(league = new League(botConfig.getRiotApiKey()));
    apis.add(new YouTubeAPI("AIzaSyCR_UuC2zxDJ8KxbFElFrCVdN4uY739HAE")); //API Key is restricted to the VM this bot runs on, don't waste your time...
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

  private static MessageFactory loadMessageFactory() {
    return new MessageFactory(sqlManager);
  }

  private static DiscordThemerImpl loadThemeManager() {
    DiscordThemerImpl themer = new DiscordThemerImpl(false);
    eventListeners.add(themer);
    return themer;
  }

  private static List<EventListener> loadEventListeners() {
    Logger.info("Loading Event Manager...");
    eventListeners.add(eventManager = new EventManager(sqlManager));

    Logger.info("Loading AutoMod...");
    eventListeners.add(new AutoMod(messageFactory));

    Logger.info("Loading Welcome Manager...");
    eventListeners.add(new WelcomeMessage(messageFactory));

    Logger.info("Loading Message Starer...");
    eventListeners.add(starMessages = new StarMessages(sqlManager));

    Logger.info("Loading Submission Pinner...");
    eventListeners.add(new SubmitPinner());

    Logger.info("Loading Leveling Manager...");
    eventListeners.add(leveling = new Leveling(sqlManager, messageFactory));

    return eventListeners;
  }

  private static void loadCommandFactory() {

    commandFactory = new CommandFactory();

    //Loads all of our commands into JDA-Util's command handler.
    commandFactory.addCommands(

        //General
        new HelpCommand(commandFactory),
        new PingCommand(),
        new RulesCommand(),
        new ApplyCommand(),
        new RewardsCommand(),
        new MentionCommand(),
        new SeasonCommand(),
        new SelfWarningsCommand(warningManager),
        new ReportCommand(reportManager),
        new WhoIsCommand(),
        new MemberCountCommand(),

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
        new ShopCommand(purchaseManager),
        new ReclaimCommand(purchaseManager),
        new MemeCommand(reddit),
        new ShippingCommand(),
        new FactCommand(),
        new SelfStarCommands(starMessages),
        new LevelCommand(leveling),
        new LeaderboardCommand(leveling),
        new RobCommand(purchaseManager),
        new DiceCommand(),
        new AvatarCommand(),
        new RemindCommand(eventManager),
        new RemindersCommand(eventManager),
        new EditRemindCommand(eventManager),
        new DeleteRemindCommand(eventManager),

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
        new MessageFactoryCommand(messageFactory),
        new EmoteCommand(),

        //Bot Management

        new RuntimeCommand(),
        new ThemeCommand(themeManager),
//                new ThemeManagerCommand(themeManager),
        new ShutdownCommand(),
        new UpdateCommand(messageFactory),
        new EvalCommand(),
        new TestCommand(),
        new AddUserCommand(leveling)

    );
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

}
