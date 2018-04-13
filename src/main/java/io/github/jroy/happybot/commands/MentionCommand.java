package io.github.jroy.happybot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;

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
            StringBuilder sb = new StringBuilder();
            if (e.getArgs().contains("twitter")) {
                if (C.toggleRole(e.getMember(), Roles.TWITTER)) {
                    sb.append("You will now receive notifications when happyheart tweets!").append("\n");
                } else {
                    sb.append("You will no longer receive notifications when happyheart tweets!").append("\n");
                }
            }
            if (e.getArgs().contains("git")) {
                if (C.toggleRole(e.getMember(), Roles.GIT)) {
                    sb.append("You will now receive notifications from git!").append("\n");
                } else {
                    sb.append("You will no longer receive notifications from git!").append("\n");
                }
            }
            if (e.getArgs().contains("updates")) {
                if (C.toggleRole(e.getMember(), Roles.UPDATES)) {
                    sb.append("You will now receive notifications for updates!").append("\n");
                } else {
                    sb.append("You will no longer receive notifications for updates!").append("\n");
                }
            }
            if (sb.toString().isEmpty()) {
                e.replyError("**Correct Usage:** ^" + name + " " + arguments);
                return;
            }
            e.reply(sb.append("Applied requested roles!").toString());
        } else {
            e.replyError("**Correct Usage:** ^" + name + " " + arguments);
        }
    }
}
