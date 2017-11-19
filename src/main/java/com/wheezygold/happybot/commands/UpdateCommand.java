package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Roles;

import java.util.concurrent.TimeUnit;

public class UpdateCommand extends Command {

    public UpdateCommand() {
        this.name = "update";
        this.arguments = "<j(enkins)/d(ropbox)>";
        this.help = "Downloads new code for the bot!";
        this.guildOnly = false;
        this.category = new Category("Bot Management");
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
                exitCode = 20;
            } else if (e.getArgs().equalsIgnoreCase("dropbox") || e.getArgs().equalsIgnoreCase("d")) {
                e.reply(":white_check_mark: Downloading Update from Dropbox!");
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
            C.log("Updater - Updating Builds with exit code: " + String.valueOf(exitCode));
            System.exit(exitCode);
        }
    }

}
