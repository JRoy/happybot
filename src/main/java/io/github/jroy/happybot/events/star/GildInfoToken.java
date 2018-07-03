package io.github.jroy.happybot.events.star;

import net.dv8tion.jda.core.entities.Message;

import java.util.ArrayList;
import java.util.List;

class GildInfoToken {

    private final String gilderId;
    private final String targetId;

    private List<Message> causedMessages = new ArrayList<>();

    GildInfoToken(String gilderId, String targetId) {
        this.gilderId = gilderId;
        this.targetId = targetId;
    }

    public void addCaused(Message message) {
        causedMessages.add(message);
    }

    public String getGilderId() {
        return gilderId;
    }

    public List<Message> getCausedMessages() {
        return causedMessages;
    }

    public String getTargetId() {
        return targetId;
    }
}
