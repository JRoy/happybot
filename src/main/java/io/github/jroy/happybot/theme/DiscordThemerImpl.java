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

import java.util.List;

public class DiscordThemerImpl extends ListenerAdapter {

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
