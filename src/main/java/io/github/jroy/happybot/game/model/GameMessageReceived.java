package io.github.jroy.happybot.game.model;

import io.github.jroy.happybot.game.ActiveGame;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@Getter
public class GameMessageReceived {

  private final ActiveGame activeGame;
  private final Member member;
  private final TextChannel textChannel;
  private final String content;

  public GameMessageReceived(ActiveGame activeGame, GuildMessageReceivedEvent e) {
    this.activeGame = activeGame;
    this.member = e.getMember();
    this.textChannel = e.getChannel();
    this.content = e.getMessage().getContentRaw();
  }
}
