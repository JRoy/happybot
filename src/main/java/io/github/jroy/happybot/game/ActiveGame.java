package io.github.jroy.happybot.game;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.Webhook;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookMessageBuilder;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

public class ActiveGame {

  @Getter
  private int id;
  @Getter
  private TextChannel channel;
  @Getter
  private Game game;
  @Getter
  private Member creator;
  @Getter
  private Set<Member> players;

  private Webhook webhook;
  @Getter
  @Setter
  private OffsetDateTime lastAction;

  public ActiveGame(int id, TextChannel channel, Webhook webhook, Game game, Member creator, Set<Member> players) {
    this.id = id;
    this.channel = channel;
    this.game = game;
    this.creator = creator;
    this.players = players;
    this.webhook = webhook;
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
}
