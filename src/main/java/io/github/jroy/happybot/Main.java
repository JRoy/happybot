package io.github.jroy.happybot;

import com.jagrosh.jdautilities.command.CommandClient;
import io.github.jroy.happybot.apis.APIBase;
import io.github.jroy.happybot.apis.Hypixel;
import io.github.jroy.happybot.apis.League;
import io.github.jroy.happybot.apis.TwitterCentre;
import io.github.jroy.happybot.apis.exceptions.IllegalAPIState;
import io.github.jroy.happybot.apis.reddit.Reddit;
import io.github.jroy.happybot.apis.youtube.YouTubeAPI;
import io.github.jroy.happybot.commands.*;
import io.github.jroy.happybot.commands.base.CommandFactory;
import io.github.jroy.happybot.commands.levels.*;
import io.github.jroy.happybot.commands.money.*;
import io.github.jroy.happybot.commands.og.OgCommand;
import io.github.jroy.happybot.commands.og.OgMngmtCommand;
import io.github.jroy.happybot.commands.og.SelfOgMngmtCommand;
import io.github.jroy.happybot.commands.remind.DeleteRemindCommand;
import io.github.jroy.happybot.commands.remind.EditRemindCommand;
import io.github.jroy.happybot.commands.remind.RemindCommand;
import io.github.jroy.happybot.commands.remind.RemindersCommand;
import io.github.jroy.happybot.commands.report.EditReportCommand;
import io.github.jroy.happybot.commands.report.HandleReportCommand;
import io.github.jroy.happybot.commands.report.LookupReportCommand;
import io.github.jroy.happybot.commands.report.ReportCommand;
import io.github.jroy.happybot.commands.warn.*;
import io.github.jroy.happybot.events.*;
import io.github.jroy.happybot.events.star.StarMessages;
import io.github.jroy.happybot.game.GameManager;
import io.github.jroy.happybot.levels.Leveling;
import io.github.jroy.happybot.sql.*;
import io.github.jroy.happybot.sql.og.OGCommandManager;
import io.github.jroy.happybot.sql.timed.EventManager;
import io.github.jroy.happybot.theme.DiscordThemerImpl;
import io.github.jroy.happybot.util.BotConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"FieldCanBeLocal"})
@Slf4j
public class Main extends ListenerAdapter {

  private static final List<EventListener> eventListeners = new ArrayList<>();
  private static YamlFile yamlFile;
  private static BotConfig botConfig;
  @Getter
  private static JDA jda;
  private static CommandFactory commandFactory;
  private static TwitterCentre twitterCentre;
  private static SQLManager sqlManager;
  private static PurchaseManager purchaseManager;
  private static WarningManager warningManager;
  private static ReportManager reportManager;
  private static OGCommandManager ogCommandManager;
  private static Hypixel hypixel;
  private static DiscordThemerImpl themeManager;
  private static MessageFactory messageFactory;
  private static EventManager eventManager;
  private static GameManager gameManager;
  private static League league;
  private static Reddit reddit;
  private static StarMessages starMessages;
  private static Leveling leveling;
  @Getter
  private static CommandClient commandClient;
  private final Main instance = this;

  public static void main(String[] args) throws IOException, IllegalArgumentException, LoginException {

    System.setProperty("http.agent", "happybot");
    System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");

    log.info("Initializing happybot...");

    log.info("Loading Config Files...");

    yamlFile = new YamlFile("settings.yml");

    if (!yamlFile.exists()) {
      yamlFile.createNewFile(true);
    }
    try {
      log.info("Reading Config...");
      yamlFile.load();
      loadConfig();
    } catch (InvalidConfigurationException e) {
      log.error("Un-parsable Settings!");
      e.printStackTrace();
      System.exit(1);
    }

    loadApis();

    themeManager = loadThemeManager();

    //Load our SQL Stuff
    sqlManager = new SQLManager(botConfig.getSqlPassword());

    purchaseManager = new PurchaseManager(sqlManager);

    messageFactory = loadMessageFactory();

    log.info("Loading Warning Manager...");
    warningManager = new WarningManager(sqlManager);

    log.info("Loading Report Manager...");
    reportManager = new ReportManager(sqlManager);

    List<EventListener> eventListeners = loadEventListeners();
    loadCommandFactory();

    log.info("Constructing JDA Instance...");
    JDABuilder builder = new JDABuilder(botConfig.getBotToken())
        .setStatus(OnlineStatus.DO_NOT_DISTURB)
        //Listens to the MessageReceivedEvent.
        .addEventListeners(commandClient = commandFactory.build())
        .setActivity(Activity.playing("Loading..."));
    for (EventListener listener : eventListeners) {
      builder.addEventListeners(listener);
    }
    log.info("Logging into Discord...");
    jda = builder.build();
//        new RichPresence((JDAImpl) jda);
  }

  private static void loadConfig() throws IOException, InvalidConfigurationException {
    copyResource("logging.properties");

    yamlFile.save();
    yamlFile.load();

    String token = getAndSet(yamlFile, "token");
    String hypixel = getAndSet(yamlFile, "hypixel-api-key");
    String riot = getAndSet(yamlFile, "riot-api-key");
    String youtubeApiKey = getAndSet(yamlFile, "youtube-api-key");
    String sql = getAndSet(yamlFile, "sql-password");
    String prefix = getAndSet(yamlFile, "prefix");
    String alternativePrefix = getAndSet(yamlFile, "alternative-prefix");
    String twitterOKey = getAndSet(yamlFile, "twitter.oauth-key");
    String twitterOSecret = getAndSet(yamlFile, "twitter.oauth-secret");
    String twitterAToken = getAndSet(yamlFile, "twitter.access-token");
    String twitterASecret = getAndSet(yamlFile, "twitter.access-token-secret");

    yamlFile.save();
    yamlFile.load();

    botConfig = new BotConfig(token, hypixel, riot, youtubeApiKey, sql, prefix, alternativePrefix, twitterOKey, twitterOSecret, twitterAToken, twitterASecret);
    log.info("Loaded Config!");
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
    log.info("Initializing APIs...");
    List<APIBase> apis = new ArrayList<>();
    apis.add(reddit = new Reddit());
    apis.add(hypixel = new Hypixel(botConfig.getHypixelApiKey()));
    apis.add(twitterCentre = new TwitterCentre(botConfig.getTwitterOauthKey(), botConfig.getTwitterOauthSecret(), botConfig.getTwitterAccessToken(), botConfig.getTwitterAccessTokenSecret()));
    apis.add(league = new League(botConfig.getRiotApiKey()));
    apis.add(new YouTubeAPI(botConfig.getYoutubeApiKey()));
    log.info("Logging into APIs...");
    for (APIBase api : apis) {
      try {
        api.loginApi();
      } catch (IllegalAPIState illegalAPIState) {
        log.error("Could not not load an API: " + illegalAPIState.getMessage());
      }
    }
    log.info("Loaded APIS:");
    for (APIBase api : apis) {
      log.info("- " + api.getApiName());
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
    log.info("Loading Game Manager...");
    eventListeners.add(gameManager = new GameManager(sqlManager));

    log.info("Loading Event Manager...");
    eventListeners.add(eventManager = new EventManager(sqlManager, gameManager));

    log.info("Loading AutoMod...");
    eventListeners.add(new AutoMod(messageFactory));

    log.info("Loading Welcome Manager...");
    eventListeners.add(new WelcomeMessage(messageFactory));

    log.info("Loading Message Starer...");
    eventListeners.add(starMessages = new StarMessages(sqlManager));

    log.info("Loading Submission Pinner...");
    eventListeners.add(new SubmitPinner());

    log.info("Loading Leveling Manager...");
    eventListeners.add(leveling = new Leveling(sqlManager, messageFactory, purchaseManager));

    log.info("Loading Game-True-False");
    eventListeners.add(new TrueFalseGame());

    log.info("Loading OG Command Manager...");
    eventListeners.add(ogCommandManager = new OGCommandManager(sqlManager));

    log.info("Loading Fanart Pinner...");
    eventListeners.add(new FanartPinner());

    eventListeners.add(new Main());

    return eventListeners;
  }

  private static void loadCommandFactory() {

    commandFactory = new CommandFactory(botConfig.getPrefix(), botConfig.getAlternativePrefix());

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
        new RoleInfoCommand(themeManager),

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
        new OldLevelCommand(leveling),
        new LeaderboardCommand(leveling),
        new RobCommand(purchaseManager),
        new DiceCommand(),
        new AvatarCommand(),
        new RemindCommand(eventManager),
        new RemindersCommand(eventManager),
        new EditRemindCommand(eventManager),
        new DeleteRemindCommand(eventManager),
        new SelfOgMngmtCommand(ogCommandManager),
        new OgMngmtCommand(ogCommandManager),
        new GameCommand(gameManager),

        //Staff Tools

        new WarnCommand(warningManager, messageFactory),
        new EditWarningCommand(warningManager),
        new DeleteWarnCommand(warningManager),
        new WarningsCommand(warningManager),
        new SpamCommand(eventManager),
        new OgCommand(),
        new FansCommand(leveling),
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
        new RestrictEmoteCommand(),
        new StarGoalCommand(starMessages),
        new SlowmodeCommand(),

        //Bot Management

        new RuntimeCommand(),
        new ThemeCommand(themeManager),
//                new ThemeManagerCommand(themeManager),
        new ShutdownCommand(gameManager),
        new UpdateCommand(messageFactory, gameManager),
        new EvalCommand(),
        new TestCommand(),
        new AddUserCommand(leveling),
        new SetXpCommand(leveling),
        new ToggleCommand(commandFactory)

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

  private static boolean copyResource(String resource) throws IOException {
    File file = new File(resource);
    if (file.exists()) {
      return false;
    }

    InputStream configResource = Main.class.getResourceAsStream("/" + resource);
    FileOutputStream stream = new FileOutputStream(file);

    byte[] buf = new byte[1024];
    int read;
    while ((read = configResource.read(buf)) != -1) {
      stream.write(buf, 0, read);
    }
    return true;
  }

  @Override
  public void onReady(@NotNull ReadyEvent event) {
    new LoggingFactory();
    log.info("Bot has been loaded & Connected to Discord!");
  }
}
