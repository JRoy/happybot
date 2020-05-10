package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.C;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class DiceCommand extends CommandBase {

  public DiceCommand() {
    super("dice", "[# of dice]d[sides]", "Roll dice.", CommandCategory.FUN);
    this.aliases = new String[]{"die", "roll"};
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    String[] split = e.getArgs().split("d");
    if (split.length != 2) {
      e.replyError(invalid);
      return;
    }

    int dice;
    int sides;

    try {
      dice = Integer.parseInt(split[0]);
      if (dice < 1) {
        throw new NumberFormatException("The amount of dice must be one or greater");
      } else if (dice > 250) {
        throw new NumberFormatException("Too many dice!");
      }
    } catch (NumberFormatException ex) {
      e.replyError(ex.getMessage());
      return;
    }
    try {
      sides = Integer.parseInt(split[1]);
      if (sides < 2) {
        throw new NumberFormatException("The amount of sides must be two or greater.");
      } else if (sides > 1_000_000) {
        throw new NumberFormatException("Too many sides!");
      }
    } catch (NumberFormatException ex) {
      e.replyError(ex.getMessage());
      return;
    }

    List<Integer> rolls = new ArrayList<>();
    for (int i = 0; i < dice; i++) {
      rolls.add(ThreadLocalRandom.current().nextInt(sides) + 1);
    }

    int sum = rolls.stream().reduce(Integer::sum).get();
    e.replySuccess(":game_die: " + C.bold("Rolled: ") + joinRolls(rolls) + "\n"
        + C.bold("Highest: ") + rolls.stream().max(Integer::compare).get() + "\n"
        + C.bold("Lowest: ") + rolls.stream().min(Integer::compare).get() + "\n"
        + C.bold("Average: ") + sum / (double) rolls.size() + "\n"
        + C.bold("Sum: ") + sum);
  }

  private String joinRolls(List<Integer> rolls) {
    return rolls.stream().map(String::valueOf).collect(Collectors.joining(", "));
  }
}
