package io.github.jroy.happybot.sql.timed;

public enum EventType {

    XP("xp"),
    MUTE("mute");

    private String translation;

    EventType(String translation) {
        this.translation = translation;
    }

    @Override
    public String toString() {
        return translation;
    }

    public static EventType getFromTranslation(String translation) {
        for (EventType curType : EventType.values()) {
            if (curType.toString().equalsIgnoreCase(translation))
                return curType;
        }
        return null;
    }

}
