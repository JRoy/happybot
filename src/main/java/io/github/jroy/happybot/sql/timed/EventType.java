package io.github.jroy.happybot.sql.timed;

public enum EventType {

  XP("xp"),
  MUTE("mute"),
  REMIND("remind");

  private final String translation;

  EventType(String translation) {
    this.translation = translation;
  }

  public static EventType getFromTranslation(String translation) {
    for (EventType curType : EventType.values()) {
      if (curType.toString().equalsIgnoreCase(translation)) {
        return curType;
      }
    }
    return null;
  }

  @Override
  public String toString() {
    return translation;
  }

}
