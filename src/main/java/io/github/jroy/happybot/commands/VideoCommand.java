package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.C;

public class VideoCommand extends CommandBase {

  public VideoCommand() {
    super("randomvid", null, "Selects a random happyheart video!", CommandCategory.FUN);
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    String randomVideo = C.urlExpand("https://mityurl.com/y/MEzr/r");
    if (randomVideo != null) {
      e.replySuccess("Here you go: " + randomVideo);
    } else {
      e.replyError("Sorry :cry:... The YouTube API is having a temporary hiccup.");
    }
  }
}
