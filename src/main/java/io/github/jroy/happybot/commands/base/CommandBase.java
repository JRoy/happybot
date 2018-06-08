package io.github.jroy.happybot.commands.base;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;
import org.jetbrains.annotations.NotNull;

/**
 * A custom implementation of JDA-Utilities's {@link com.jagrosh.jdautilities.command.Command Command} class that makes our use-case easier.
 */
public abstract class CommandBase extends Command {

    private final Roles permissionRole;

    /**
     *
     * Constructor for commands with no role permissions requires for execution.
     *
     * @param commandName Command's name to be used for execution.
     * @param arguments Arguments of the command to be used for command help.
     * @param helpMessage Command description to be used inside the command list.
     * @param category Command's category to be used inside the command list.
     */
    public CommandBase(@NotNull String commandName, String arguments, String helpMessage, CommandCategory category) {
        this.name = commandName;
        this.arguments = arguments;
        this.help = helpMessage;
        this.category = new Category(category.toString());
        this.permissionRole = null;
    }

    /**
     *
     * Constructor for commands with certain role requires for execution.
     *
     * @param commandName Command's name to be used for execution.
     * @param arguments Arguments of the command to be used for command help.
     * @param helpMessage Command description to be used inside the command list.
     * @param category Command's category to be used inside the command list.
     * @param permissionRole The role requires to execute the command.
     */
    public CommandBase(@NotNull String commandName, String arguments, String helpMessage, CommandCategory category, Roles permissionRole) {
        this.name = commandName;
        this.arguments = arguments;
        this.help = helpMessage;
        this.category = new Category(category.toString());
        this.permissionRole = permissionRole;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (permissionRole != null) {
            if (!C.hasRole(event.getMember(), permissionRole)) {
                event.replyError(C.permMsg(permissionRole));
                return;
            }
        }
        executeCommand(new io.github.jroy.happybot.commands.base.CommandEvent(event.getEvent(), event.getArgs(), event.getClient()));
    }

    /**
     * Triggered when the command is ran and the user has the required permission.
     * @param event The information associated with the command calling
     */
    protected abstract void executeCommand(io.github.jroy.happybot.commands.base.CommandEvent event);

    /**
     * @return Command usage.
     */
    protected String invalid() {
        return "**Correct Usage:** ^" + name + " " + arguments;
    }

}
