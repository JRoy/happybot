package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.wheezygold.happybot.util.*;
import net.dv8tion.jda.core.EmbedBuilder;

import java.util.concurrent.TimeUnit;

public class UpdateCommand extends Command {

    private MessageFactory messageFactory;

    public UpdateCommand(MessageFactory messageFactory) {
        this.name = "update";
        this.arguments = "<j(enkins)/d(ropbox)>";
        this.help = "Downloads new code for the bot!";
        this.guildOnly = false;
        this.category = new Category("Bot Management");
        this.messageFactory = messageFactory;
    }

    @Override
    protected void execute(CommandEvent e) {
        if (C.hasRole(e.getMember(), Roles.DEVELOPER)) {
            new Thread(new Update(e)).start();
        } else {
            e.replyError(C.permMsg(Roles.DEVELOPER));
        }
    }

    class Update implements Runnable {

        private CommandEvent e;

        Update(CommandEvent e) { this.e = e; }

        @Override
        public void run() {
            int exitCode;
            if (e.getArgs().equalsIgnoreCase("jenkins") || e.getArgs().equalsIgnoreCase("j")) {
                e.reply(":white_check_mark: Downloading Update from Jenkins!");
                new Thread(new ImpendRestart("Jenkins")).start();
                exitCode = 20;
            } else if (e.getArgs().equalsIgnoreCase("dropbox") || e.getArgs().equalsIgnoreCase("d")) {
                e.reply(":white_check_mark: Downloading Update from Dropbox!");
                new Thread(new ImpendRestart("Dropbox")).start();
                exitCode = 10;
            } else {
                e.replyError("**Correct Usage:** ^" + name + " " + arguments);
                return;
            }
            e.reply(":information_source: Restarting Bot...");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            e.getJDA().shutdown();
            Logger.log("Updater - Updating Builds with exit code: " + String.valueOf(exitCode));
            Logger.info("[Updater] ");
            Logger.info("[Updater] Updater has stopped JDA and is impeding a new update now.");
            Logger.info("[Updater] ");
            System.exit(exitCode);
        }
    }

    class ImpendRestart implements Runnable {

        private String s;

        ImpendRestart(String source) {
            this.s = source;
        }

        @Override
        public void run() {
            Channels.BOT_META.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Impending Update")
                    .setDescription(messageFactory.getRawMessage(MessageFactory.MessageType.UPDATE_START) + "\nNew Impending Update from " + s + ". Bot is currently restarting")
                    .build()).queue();
        }
    }

}
