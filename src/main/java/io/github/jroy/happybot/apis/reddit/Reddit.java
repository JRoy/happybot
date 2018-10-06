package io.github.jroy.happybot.apis.reddit;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.jroy.happybot.apis.APIBase;
import io.github.jroy.happybot.util.BotConfig;
import io.github.jroy.happybot.util.C;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;

import java.util.Objects;

public class Reddit extends APIBase {

  private final String username;
  private final String password;
  private final String clientId;
  private final String clientSecret;

  private RedditClient redditClient;

  public Reddit(BotConfig botConfig) {
    super("Reddit");
    this.username = botConfig.getRedditUsername();
    this.password = botConfig.getRedditPassword();
    this.clientId = botConfig.getRedditClientId();
    this.clientSecret = botConfig.getRedditClientSecret();
  }

  @Override
  public void loadApi() {
    if (isValid()) {
      NetworkAdapter adapter = new OkHttpNetworkAdapter(new UserAgent("happybot", "io.github.jroy", "v0.1", username));
      Credentials credentials = Credentials.script(username, password, clientId, clientSecret);
      redditClient = OAuthHelper.automatic(adapter, credentials);
    }
  }

  public RedditClient getRedditClient() {
    return redditClient;
  }

  public MemePost getRandomMedia(String subReddit) {
    JsonElement jsonElement = new JsonParser().parse(Objects.requireNonNull(C.readUrl("https://www.reddit.com/r/" + subReddit + "/random/.json")));
    JsonObject jsonObject;
    if (jsonElement.isJsonArray()) {
      jsonObject = jsonElement.getAsJsonArray().get(0).getAsJsonObject();
    } else {
      jsonObject = jsonElement.getAsJsonObject();
    }
    return new MemePost(jsonObject.getAsJsonObject("data").getAsJsonArray("children").get(0).getAsJsonObject().getAsJsonObject("data"));
  }

  private boolean isValid() {
    return !(username.isEmpty() || password.isEmpty() || clientId.isEmpty() || clientSecret.isEmpty());
  }
}
