package io.github.jroy.happybot.commands.base;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;
import org.jetbrains.annotations.NotNull;

public abstract class CommandBase extends Command {

    private final Roles permissionRole;

    public CommandBase(@NotNull String commandName, String arguments, String helpMessage, CommandCategory category) {
        this.name = commandName;
        this.arguments = arguments;
        this.help = helpMessage;
        this.category = new Category(category.toString());
        this.permissionRole = null;
    }

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

    protected abstract void executeCommand(io.github.jroy.happybot.commands.base.CommandEvent event);

    protected String invalid() {
        return "**Correct Usage:** ^" + name + " " + arguments;
    }

}
