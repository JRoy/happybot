package io.github.jroy.happybot.commands.base;

public enum CommandCategory {

    GENERAL("General"),
    FUN("Fun"),
    STAFF("Staff Tools"),
    BOT("Bot Management");

    private final String translation;

    CommandCategory(String translation) {
        this.translation = translation;
    }

    @Override
    public String toString() {
        return translation;
    }
}
