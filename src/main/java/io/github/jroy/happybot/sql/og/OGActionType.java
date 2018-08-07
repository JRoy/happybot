package io.github.jroy.happybot.sql.og;

public enum OGActionType {

  COMMAND("Create Command"),
  NAME("Change Command Name"),
  CONTENT("Change Command Content");

  private String translation;

  OGActionType(String translation) {
    this.translation = translation;
  }

  public String getTranslation() {
    return translation;
  }
}
