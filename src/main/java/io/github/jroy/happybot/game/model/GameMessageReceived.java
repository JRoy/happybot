package io.github.jroy.happybot.game.model;

import io.github.jroy.happybot.game.ActiveGame;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@Getter
public class GameMessageReceived {

  private ActiveGame activeGame;
  private Member member;
  private TextChannel textChannel;
  private String content;

  public GameMessageReceived(ActiveGame activeGame, GuildMessageReceivedEvent e) {
    this.activeGame = activeGame;
    this.member = e.getMember();
    this.textChannel = e.getChannel();
    this.content = e.getMessage().getContentRaw();
  }
}
