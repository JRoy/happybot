package io.github.jroy.happybot.game.model;

import io.github.jroy.happybot.game.ActiveGame;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

public class GameReactionReceived {

  private GuildMessageReactionAddEvent event;
  private ActiveGame activeGame;

  public GameReactionReceived(GuildMessageReactionAddEvent event, ActiveGame activeGame) {
    this.event = event;
    this.activeGame = activeGame;
  }

  public GuildMessageReactionAddEvent getEvent() {
    return event;
  }

  public ActiveGame getActiveGame() {
    return activeGame;
  }
}
