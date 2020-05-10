package io.github.jroy.happybot.apis;

import net.rithms.riot.api.ApiConfig;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.constant.Platform;

import java.util.HashMap;

public class League extends APIBase {

  private RiotApi api;
  private final String apiKey;

  public League(String apiKey) {
    super("League");
    this.apiKey = apiKey;
  }

  @Override
  public void loadApi() {
    api = new RiotApi(new ApiConfig().setKey(apiKey));
  }

  public HashMap<String, String> getAllFields(String username, Platform platform) throws RiotApiException, IllegalArgumentException {
    HashMap<String, String> fields = new HashMap<>();
    Summoner summoner = api.getSummonerByName(platform, username);

    if (summoner != null) {
      fields.put("Name", summoner.getName());
      fields.put("Summoner ID", String.valueOf(summoner.getId()));
      fields.put("Summoner Level", String.valueOf(summoner.getSummonerLevel()));
      fields.put("Profile Icon ID", String.valueOf(summoner.getProfileIconId()));
    }
    return fields;
  }

  public int getLevel(String name) throws RiotApiException {
    return api.getSummonerByName(Platform.NA, name).getSummonerLevel();
  }

}
