package io.github.jroy.happybot.commands.base;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import io.github.jroy.happybot.util.Constants;
import io.github.jroy.happybot.util.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandFactory {

    private CommandClientBuilder clientBuilder;
    private CommandClient commandClient;

    private List<CommandBase> registeredCommands = new ArrayList<>();
    private Map<CommandCategory, List<CommandBase>> categorizedCommands = new HashMap<>();

    public CommandFactory() {
        Logger.info("Loading Command Factory...");
        clientBuilder = new CommandClientBuilder();
        clientBuilder.setPrefix("^");
        clientBuilder.setOwnerId(Constants.OWNER_ID.get());
        clientBuilder.useHelpBuilder(false);
    }

    public void addCommands(CommandBase... commands) {
        Logger.info("Adding Commands...");
        clientBuilder.addCommands(commands);
        for (CommandBase base : commands) {
            if (!categorizedCommands.containsKey(base.getCommandCategory()))
                categorizedCommands.put(base.getCommandCategory(), new ArrayList<>());
            categorizedCommands.get(base.getCommandCategory()).add(base);
            registeredCommands.add(base);
        }
        Logger.info("Added " + commands.length + " Commands!");
    }

    public CommandClient build() {
        return commandClient = clientBuilder.build();
    }

    public List<CommandBase> getRegisteredCommands() {
        return registeredCommands;
    }

    public Map<CommandCategory, List<CommandBase>> getCategorizedCommands() {
        return categorizedCommands;
    }
}
