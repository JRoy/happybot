package io.github.jroy.happybot.commands.base;

public enum CommandCategory {

  /**
   * Commands that would be filed as meta or things that have to do with the server.
   */
  GENERAL("General"),
  /**
   * Commands that are off-topic which may not serve any real productive task.
   */
  FUN("Fun"),
  /**
   * Commands that aid in moderation.
   */
  STAFF("Staff Tools"),
  /**
   * Commands that aid in managing the bot.
   */
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
