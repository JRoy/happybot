package io.github.jroy.happybot.games;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.Webhook;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookMessageBuilder;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

public class ActiveGame extends ListenerAdapter {

  private int id;
  private TextChannel channel;
  private Game game;
  private Member creator;
  private Set<Member> players;

  private Webhook webhook;
  private OffsetDateTime lastAction;

  public ActiveGame(int id, TextChannel channel, Game game, Member creator, Set<Member> players) {
    this.id = id;
    this.channel = channel;
    this.game = game;
    this.creator = creator;
    this.players = players;
    this.webhook = channel.getWebhooks().complete().get(0);
    this.lastAction = OffsetDateTime.now().plus(10, ChronoUnit.MINUTES);
    players.add(creator);
  }

  public void sendMessage(String message) {
    sendMessage("Game #" + id, message);
  }

  public void sendMessage(String username, String message) {
    WebhookClient client = webhook.newClient().build();
    client.send(new WebhookMessageBuilder().setAvatarUrl(channel.getJDA().getSelfUser().getAvatarUrl())
        .setUsername(username)
        .setContent(message)
        .build());
    client.close();
  }

  public void sendMessage(MessageEmbed embed) {
    sendMessage("Game #" + id, embed);
  }

  public void sendMessage(String username, MessageEmbed embed) {
    WebhookClient client = webhook.newClient().build();
    client.send(new WebhookMessageBuilder().setAvatarUrl(channel.getJDA().getSelfUser().getAvatarUrl())
        .setUsername(username)
        .addEmbeds(embed)
        .build());
    client.close();
  }

  public Game getGame() {
    return game;
  }

  public int getId() {
    return id;
  }

  public TextChannel getChannel() {
    return channel;
  }

  public Set<Member> getPlayers() {
    return players;
  }

  public Member getCreator() {
    return creator;
  }

  public void setLastAction(OffsetDateTime lastAction) {
    this.lastAction = lastAction;
  }

  public OffsetDateTime getLastAction() {
    return lastAction;
  }
}
