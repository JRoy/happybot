package io.github.jroy.happybot.apis;


import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import me.kbrewster.exceptions.APIException;
import me.kbrewster.exceptions.InvalidPlayerException;
import me.kbrewster.hypixelapi.HypixelAPI;
import me.kbrewster.hypixelapi.player.HypixelPlayer;

public class Hypixel extends APIBase {

  private final String apiKey;
  private HypixelAPI api;

  public Hypixel(String apiKey) {
    super("Hypixel");
    this.apiKey = apiKey;
  }

  @Override
  public void loadApi() {
    api = new HypixelAPI(apiKey);
  }

  public HypixelPlayer getPlayer(String playerName) throws APIException {
    try {
      return api.getPlayer(playerName);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public HashMap<String, String> getAllFields(HypixelPlayer hypixelPlayer) {
    HashMap<String, String> fields = new HashMap<>();
    if (hypixelPlayer != null) {
      DecimalFormat df = new DecimalFormat("#.#");
      fields.put("Network Level", df.format(hypixelPlayer.getAbsoluteLevel()));
      fields.put("Rank", hypixelPlayer.getCurrentRank());
      fields.put("MC Version", hypixelPlayer.getMcVersionRp());
      fields.put("Bedwars Wins", String.valueOf(hypixelPlayer.getAchievements().getBedwarsWins()));
      fields.put("Bedwars Level", String.valueOf(hypixelPlayer.getAchievements().getBedwarsLevel()));
      fields.put("Karma", String.valueOf(hypixelPlayer.getKarma()));
      fields.put("Language", hypixelPlayer.getUserLanguage());
      fields.put("Vanity Tokens", String.valueOf(hypixelPlayer.getVanityTokens()));
      fields.put("Join Date", new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date(hypixelPlayer.getFirstLogin())));
      fields.put("Last Join", new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date(hypixelPlayer.getLastLogin())));
    }
    return fields;
  }

  public boolean isValidPlayer(String playerName) {

    if (playerName.contains(" ")) {
      return false;
    }

    try {
      api.getPlayer(playerName);
      return true;
    } catch (APIException | IOException e) {
      e.printStackTrace();
      return false;
    } catch (InvalidPlayerException ie) {
      return false;
    }
  }


}
