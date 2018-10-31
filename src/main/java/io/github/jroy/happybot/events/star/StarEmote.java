package io.github.jroy.happybot.events.star;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum StarEmote {
  STAR("⭐", "starred", "https://i.imgur.com/WwPHcgU.png"),
  HEEL("\uD83D\uDC60", "heeled", "https://i.imgur.com/Gq5xooX.png");

  private final String name;
  private final String action;
  private final String iconUrl;

  public static StarEmote getByName(String name) {
    for(StarEmote emote : values()) {
      if(emote.getName().equals(name)) {
        return emote;
      }
    }
    return null;
  }
}
