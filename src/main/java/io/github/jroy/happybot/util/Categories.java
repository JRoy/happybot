package io.github.jroy.happybot.util;

import net.dv8tion.jda.api.entities.Category;

public enum Categories {

  META("356261428535099394"),
  DISCUSSION("356261794656026624"),
  GAMES("499692704293322773"),
  SUBMISSIONS("356262362812252161"),
  STAFF_META("356263177668788236"),
  STAFF_DISCUSSION("356263399111262208"),
  VOICE("358051174487818240");

  private final String categoryId;

  Categories(String categoryId) {
    this.categoryId = categoryId;
  }

  public Category getCategory() {
    return C.getGuild().getCategoryById(categoryId);
  }

  public String getCategoryId() {
    return categoryId;
  }
}
