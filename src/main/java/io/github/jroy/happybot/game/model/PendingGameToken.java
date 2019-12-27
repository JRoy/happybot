package io.github.jroy.happybot.game.model;

import io.github.jroy.happybot.game.Game;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.HashSet;
import java.util.Set;

@Getter
public class PendingGameToken {
  private final String promptId;
  private final Message message;
  private final Member member;
  private final Game game;
  private final Set<Member> players = new HashSet<>();

  public PendingGameToken(String promptId, Message message, Member member, Game game) {
    this.promptId = promptId;
    this.message = message;
    this.member = member;
    this.game = game;
    this.players.add(member);
  }

  public void addPlayer(Member member) {
    players.add(member);
  }
}
