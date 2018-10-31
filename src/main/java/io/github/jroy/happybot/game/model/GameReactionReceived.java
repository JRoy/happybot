package io.github.jroy.happybot.game.model;

import io.github.jroy.happybot.game.ActiveGame;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

@RequiredArgsConstructor
@Getter
public class GameReactionReceived {
  private final GuildMessageReactionAddEvent event;
  private final ActiveGame activeGame;
}
