package io.github.jroy.happybot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Logger;
import io.github.jroy.happybot.util.Roles;

public class ShutdownCommand extends Command {

    public ShutdownCommand() {
        this.name = "shutdown";
        this.arguments = "<instance/vm>";
        this.help = "Shutdowns the bot with love.";
        this.guildOnly = false;
        this.category = new Category("Bot Management");
    }

    @Override
    protected void execute(CommandEvent e) {
        if (e.isOwner()) {
            if (e.getArgs().startsWith("instance")) {
                e.getMessage().addReaction("✅").queue();
                e.replySuccess(":white_check_mark: Stopping the JDA Instance!");
                e.getJDA().shutdown();
                Logger.log("The JDA instance has been shutdown.");
            } else if (e.getArgs().startsWith("vm")) {
                e.getMessage().addReaction("✅").queue();
                e.replySuccess(":white_check_mark: Stopping the VM!");
                e.getJDA().shutdown();
                Logger.log("The JDA instance has been shutdown...exiting the program.");
                System.exit(0);
            } else {
                e.replyError("**Correct Usage:** ^" + name + " " + arguments);
            }
        } else {
            e.replyError(C.permMsg(Roles.DEVELOPER));
        }
    }

}