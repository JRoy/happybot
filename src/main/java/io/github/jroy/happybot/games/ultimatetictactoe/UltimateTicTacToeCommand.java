package io.github.jroy.happybot.games.ultimatetictactoe;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.awt.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class UltimateTicTacToeCommand extends CommandBase {
  private UltimateTicTacToeManager manager;

  public UltimateTicTacToeCommand(UltimateTicTacToeManager manager) {
    super("utimatettt", "", "Start a game of Ultimate Tic-Tac-Toe", CommandCategory.FUN);
    this.aliases = new String[] {"ultimatetictactoe", "uttt"};
    this.manager = manager;
  }

  @Override
  protected void executeCommand(CommandEvent event) {
    MessageChannel channel = event.getChannel();
    if (manager.getGame(channel.getIdLong()) != null) {
      channel.sendMessage("A game is already in progress!").queue();
      return;
    } else if (manager.isWaiting(event.getAuthor())) {
      channel.sendMessage("You are already waiting for a game!").queue();
      return;
    }

    Message sent = event.getMessage();
    channel.sendMessage(new EmbedBuilder()
        .setTitle("Ultimate Tic-Tac-Toe")
        .setDescription(sent.getMember().getEffectiveName() + " wants to start a game of Ultimate Tic-Tac-Toe!\n" +
            "React to this message to start!")
        .setFooter("Expires", null)
        .setTimestamp(Instant.now().plus(5, ChronoUnit.MINUTES))
        .setColor(Color.YELLOW)
        .build()
    ).queue(message -> message.addReaction("\uD83C\uDFB2").queue(
        success -> manager.putWaiting(message.getIdLong(), sent.getAuthor())));
  }
}
