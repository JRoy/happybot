package io.github.jroy.happybot.games.model;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class GameMessageReceived {

  private Member member;
  private TextChannel textChannel;
  private String content;

  public GameMessageReceived(GuildMessageReceivedEvent e) {
    this.member = e.getMember();
    this.textChannel = e.getChannel();
    this.content = e.getMessage().getContentRaw();
  }

  public Member getMember() {
    return member;
  }

  public TextChannel getTextChannel() {
    return textChannel;
  }

  public String getContent() {
    return content;
  }
}
