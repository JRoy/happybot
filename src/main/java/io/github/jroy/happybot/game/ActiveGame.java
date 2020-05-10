package io.github.jroy.happybot.game;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Set;

public class ActiveGame {

  @Getter
  private final int id;
  @Getter
  private final TextChannel channel;
  @Getter
  private final Game game;
  @Getter
  private final Member creator;
  @Getter
  private final Set<Member> players;

  private final Webhook webhook;
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
    StringBuilder sb = new StringBuilder();
    sb.append("Hello");
    for (Member curPlayer : players) {
      sb.append(" ").append(curPlayer.getAsMention());
    }
    sb.append("! This is your private game channel.");
    sendMessage(sb.toString());
  }

  public void sendMessage(String message) {
    sendMessage("Game #" + id, message);
  }

  public void sendMessage(String username, String message) {
    WebhookClient client = WebhookClient.withId(webhook.getIdLong(), Objects.requireNonNull(webhook.getToken()));
    client.send(new WebhookMessageBuilder().setAvatarUrl(channel.getJDA().getSelfUser().getAvatarUrl())
        .setUsername(username)
        .setContent(message)
        .build());
    client.close();
  }

  public void sendMessage(WebhookEmbed embed) {
    sendMessage("Game #" + id, embed);
  }

  public void sendMessage(String username, WebhookEmbed embed) {
    WebhookClient client = WebhookClient.withId(webhook.getIdLong(), Objects.requireNonNull(webhook.getToken()));
    client.send(new WebhookMessageBuilder()
        .setAvatarUrl(channel.getJDA().getSelfUser().getAvatarUrl())
        .setUsername(username)
        .addEmbeds(embed)
        .build());
    client.close();
  }
}
