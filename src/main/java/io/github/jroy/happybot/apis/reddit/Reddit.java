package io.github.jroy.happybot.apis.reddit;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.jroy.happybot.apis.APIBase;
import io.github.jroy.happybot.util.C;

import java.util.Objects;

public class Reddit extends APIBase {

  public Reddit() {
    super("Reddit");
  }

  @Override
  public void loadApi() {
  }

  public MemePost getRandomMedia(String subReddit) {
    JsonElement jsonElement = JsonParser.parseString(Objects.requireNonNull(C.readUrl("https://www.reddit.com/r/" + subReddit + "/random/.json")));
    JsonObject jsonObject;
    if (jsonElement.isJsonArray()) {
      jsonObject = jsonElement.getAsJsonArray().get(0).getAsJsonObject();
    } else {
      jsonObject = jsonElement.getAsJsonObject();
    }
    return new MemePost(jsonObject.getAsJsonObject("data").getAsJsonArray("children").get(0).getAsJsonObject().getAsJsonObject("data"));
  }
}
