package io.github.jroy.happybot.events.star;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
class GildInfoToken {
  private final String gilderId;
  private final String targetId;
  private final List<Message> causedMessages = new ArrayList<>();

  public void addCaused(Message message) {
    causedMessages.add(message);
  }
}
