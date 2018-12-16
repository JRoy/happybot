package io.github.jroy.happybot.theme;

import io.github.jroy.happybot.util.Constants;
import io.github.wheezygold7931.discordthemer.DiscordThemer;
import io.github.wheezygold7931.discordthemer.DiscordThemerBuilder;
import io.github.wheezygold7931.discordthemer.ThemeToken;
import io.github.wheezygold7931.discordthemer.exceptions.ThemeNotFoundException;
import io.github.wheezygold7931.discordthemer.util.ActionMode;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.StatusChangeEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscordThemerImpl extends ListenerAdapter {
  private static Field FIELD_THEME_MAP;

  static {
    try {
      FIELD_THEME_MAP = DiscordThemer.class.getDeclaredField("themeMap");
      FIELD_THEME_MAP.setAccessible(true);
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }
  }

  private final boolean debugMode;
  private DiscordThemer discordThemer;

  public DiscordThemerImpl(boolean debugMode) {
    this.debugMode = debugMode;
  }

  @Override
  public void onStatusChange(StatusChangeEvent event) {
    if (event.getNewStatus() == JDA.Status.CONNECTED && discordThemer == null) {
      discordThemer = new DiscordThemerBuilder(event.getJDA())
          .setGuild(event.getJDA().getGuildById(Constants.GUILD_ID.get()))
          .setActionMode(ActionMode.QUEUE)
          .setThemeFolder("themes/")
          .setLogDisplayWarnings(true)
          .setDebugMode(debugMode)
          .build();
    }
  }

  public Map<String, String> getRoleNames(String role) {
    Map<String, String> names = new HashMap<>();
    try {
      @SuppressWarnings("unchecked")
      Map<String, ThemeToken> themeMap = (Map<String, ThemeToken>) FIELD_THEME_MAP.get(discordThemer);
      for (Map.Entry<String, ThemeToken> theme : themeMap.entrySet()) {
        String name = theme.getValue().getThemeRoleData().get(role);
        if (name != null) {
          names.put(theme.getKey(), name);
        }
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
    return names;
  }

  public boolean isValidTheme(String themeName) {
    return discordThemer.isValidTheme(themeName);
  }

  public ThemeToken getThemeToken(String themeName) {
    return null;
  }

  public void switchToTheme(String themeName) throws ThemeNotFoundException {
    discordThemer.setServerTheme(themeName);
  }

  public List<String> getThemeList() {
    return null;
  }

}
