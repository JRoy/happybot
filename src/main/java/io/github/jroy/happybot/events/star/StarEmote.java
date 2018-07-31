package io.github.jroy.happybot.events.star;

public enum StarEmote {
  STAR("⭐", "starred", "https://i.imgur.com/WwPHcgU.png"),
  HEEL("\uD83D\uDC60", "heeled", "https://i.imgur.com/Gq5xooX.png");

  private String name;
  private String action;
  private String iconUrl;

  StarEmote(String name, String action, String iconUrl) {
    this.name = name;
    this.action = action;
    this.iconUrl = iconUrl;
  }

  public String getName() {
    return name;
  }

  public String getAction() {
    return action;
  }

  public String getIconUrl() {
    return iconUrl;
  }

  public static StarEmote getByName(String name) {
    for(StarEmote emote : values()) {
      if(emote.getName().equals(name)) {
        return emote;
      }
    }
    return null;
  }
}
