package io.github.jroy.happybot.games.model;

import io.github.jroy.happybot.games.Game;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import java.util.HashSet;
import java.util.Set;

public class PendingGameToken {

  private String promptId;
  private Message message;
  private Member member;
  private Game game;

  private Set<Member> players = new HashSet<>();

  public PendingGameToken(String promptId, Message message, Member member, Game game) {
    this.promptId = promptId;
    this.message = message;
    this.member = member;
    this.game = game;
    this.players.add(member);
  }

  public String getPromptId() {
    return promptId;
  }

  public Message getMessage() {
    return message;
  }

  public Member getMember() {
    return member;
  }

  public Game getGame() {
    return game;
  }

  public void addPlayer(Member member) {
    players.add(member);
  }

  public Set<Member> getPlayers() {
    return players;
  }
}
