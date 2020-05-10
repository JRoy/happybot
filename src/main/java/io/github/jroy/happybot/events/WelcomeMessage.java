package io.github.jroy.happybot.events;

import io.github.jroy.happybot.sql.MessageFactory;
import io.github.jroy.happybot.util.Channels;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@RequiredArgsConstructor
public class WelcomeMessage extends ListenerAdapter {
  private final MessageFactory messageFactory;

  @Override
  public void onGuildMemberJoin(GuildMemberJoinEvent event) {
    String joinformat = messageFactory.getRawMessage(MessageFactory.MessageType.JOIN).replaceAll("<player>", event.getMember().getAsMention()).replaceAll("<user>", event.getMember().getAsMention());
    if (!joinformat.startsWith("'s") || !joinformat.startsWith(",")) {
      joinformat = " " + joinformat;
    }
    Channels.WELCOME.getChannel().sendMessage(event.getMember().getAsMention() + joinformat).queue();
  }

  @Override
  public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
    String leaveformat = messageFactory.getRawMessage(MessageFactory.MessageType.LEAVE).replaceAll("<player>", "**" + event.getUser().getName() + "**").replaceAll("<user>", "**" + event.getUser().getName() + "**");
    if (!leaveformat.startsWith("'s") || !leaveformat.startsWith(",")) {
      leaveformat = " " + leaveformat;
    }
    Channels.WELCOME.getChannel().sendMessage("**" + event.getUser().getName() + "**" + leaveformat).queue();
  }

}
