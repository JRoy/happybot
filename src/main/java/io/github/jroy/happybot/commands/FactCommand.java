package io.github.jroy.happybot.commands;

import com.google.gson.JsonParser;
import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.C;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Objects;

public class FactCommand extends CommandBase {

  public FactCommand() {
    super("fact", null, "Generates a random cat fact.", CommandCategory.FUN);
    this.setCooldownSeconds(10);
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    String catFactString = Objects.requireNonNull(C.readUrl("https://catfact.ninja/fact"));

    String catFact = new JsonParser().parse(catFactString)
        .getAsJsonObject().get("fact").getAsString();

    e.reply(new EmbedBuilder()
        .setTitle("Cat Fact")
        .setDescription(catFact)
        .build());
  }
}
