package io.github.jroy.happybot.game.model;

import io.github.jroy.happybot.game.ActiveGame;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class GameStartEvent {
  private final ActiveGame activeGame;
}
