package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Roles;

public class MentionCommand extends Command {
    public MentionCommand() {
        this.name = "mention";
        this.help = "Toggles when you want to be mentioned.";
        this.arguments = "<twitter/git/updates>";
        this.guildOnly = true;
        this.category = new Category("General");
    }

    @Override
    protected void execute(CommandEvent e) {
        if (!e.getArgs().isEmpty()) {
            if (e.getArgs().equals("twitter")) {
                if (C.toggleRole(e.getMember(), Roles.TWITTER)) {
                    e.reply("You will now receive notifications when happyheart tweets!");
                } else {
                    e.reply("You will no longer receive notifications when happyheart tweets!");
                }
            } else if (e.getArgs().equals("git")) {
                if (C.toggleRole(e.getMember(), Roles.GIT)) {
                    e.reply("You will now receive notifications from git!");
                } else {
                    e.reply("You will no longer receive notifications from git!");
                }
            } else if (e.getArgs().equals("updates")) {
                if (C.toggleRole(e.getMember(), Roles.UPDATES)) {
                    e.reply("You will now receive notifications for updates!");
                } else {
                    e.reply("You will no longer receive notifications for updates!");
                }
            } else {
                e.replyError("**Correct Usage:** ^" + name + " " + arguments);
            }
        } else {
            e.replyError("**Correct Usage:** ^" + name + " " + arguments);
        }
    }
}
