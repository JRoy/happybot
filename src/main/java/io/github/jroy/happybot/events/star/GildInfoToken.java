package io.github.jroy.happybot.events.star;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.entities.Message;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
class GildInfoToken {
  @Getter
  private final String gilderId;
  @Getter
  private final String targetId;

  @Getter
  private final List<Message> causedMessages = new ArrayList<>();

  public void addCaused(Message message) {
    causedMessages.add(message);
  }
}
