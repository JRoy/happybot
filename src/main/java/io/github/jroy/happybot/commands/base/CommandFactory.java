package io.github.jroy.happybot.commands.base;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import io.github.jroy.happybot.util.Constants;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class CommandFactory {

  private final CommandClientBuilder clientBuilder;

  private final Map<String, CommandBase> registeredCommands = new HashMap<>();
  private final Map<CommandCategory, List<CommandBase>> categorizedCommands = new HashMap<>();

  public CommandFactory(String prefix, String alternativePrefix) {
    log.info("Loading Command Factory...");
    clientBuilder = new CommandClientBuilder();
    clientBuilder.setPrefix(prefix);
    clientBuilder.setAlternativePrefix(alternativePrefix);
    clientBuilder.setOwnerId(Constants.OWNER_ID.get());
    clientBuilder.useHelpBuilder(false);
  }

  public void addCommands(CommandBase... commands) {
    log.info("Adding Commands...");
    clientBuilder.addCommands(commands);
    for (CommandBase base : commands) {
      if (!categorizedCommands.containsKey(base.getCommandCategory())) {
        categorizedCommands.put(base.getCommandCategory(), new ArrayList<>());
      }
      categorizedCommands.get(base.getCommandCategory()).add(base);
      registeredCommands.put(base.getName(), base);
    }
    log.info("Added " + commands.length + " Commands!");
  }

  public CommandClient build() {
    return clientBuilder.build();
  }

  public Map<String, CommandBase> getRegisteredCommands() {
    return registeredCommands;
  }

  public Map<CommandCategory, List<CommandBase>> getCategorizedCommands() {
    return categorizedCommands;
  }
}
