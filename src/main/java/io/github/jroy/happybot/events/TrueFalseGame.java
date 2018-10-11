package io.github.jroy.happybot.events;

import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Channels;
import io.github.jroy.happybot.util.Roles;
import io.github.jroy.happybot.util.SafeRestAction;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.concurrent.TimeUnit;

public class TrueFalseGame extends ListenerAdapter {

  private int curCount = 0;

  @Override
  public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
    if (e.getChannel().getId().equals(Channels.TRUE_FALSE_GAME.getId())) {
      if (isInvalidMessage(e.getMessage().getContentRaw()) && !C.hasRole(e.getMember(), Roles.DEVELOPER)) {
        new SafeRestAction(e.getMessage().delete()).wait(TimeUnit.MILLISECONDS, 500).queue();
        C.privChannel(e.getMember(), "The correct format for gameTrueFalse is (true/false), the person below me (statement)");
        return;
      }
//      if (repeatCache.contains(curCount)) {
//        if (repeatCache.get(curCount).getUser().getId().equals(e.getMember().getUser().getId()))
//          new SafeRestAction(e.getMessage().delete()).wait(TimeUnit.MILLISECONDS, 500).queue();
//        C.privChannel(e.getMember(), "You may not respond to your own true/false!");
//        return;
//      }
      curCount++;
    }
  }

  @Override
  public void onGuildMessageUpdate(GuildMessageUpdateEvent e) {
    if (e.getChannel().getId().equals(Channels.TRUE_FALSE_GAME.getId())) {
      if (!C.hasRole(e.getMember(), Roles.DEVELOPER)) {
        new SafeRestAction(e.getMessage().delete()).wait(TimeUnit.MILLISECONDS, 500).queue();
        C.privChannel(e.getMember(), "You may not edit messages in gameTrueFalse!");
      }
    }
  }

  private boolean isInvalidMessage(String message) {
    return !message.startsWith("true, the person below me ") && !message.startsWith("false, the person below me ") || message.contains("\n");
  }

}
