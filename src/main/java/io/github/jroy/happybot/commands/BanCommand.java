package io.github.jroy.happybot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.core.entities.Member;

public class BanCommand extends Command {
    public BanCommand() {
        this.name = "ban";
        this.aliases = new String[]{"begone"};
        this.help = "Bans target user from server.";
        this.arguments = "<user mention> <reason>";
        this.guildOnly = true;
        this.category = new Category("Staff Tools");
    }

    @Override
    protected void execute(CommandEvent e) {
        if (C.hasRole(e.getMember(), Roles.MODERATOR)) {
            if (C.containsMention(e)) {
                if (e.getArgs().replaceAll("<(.*?)>", "").isEmpty()) {
                    e.replyError("**Correct Usage:** ^" + name + " " + arguments);
                    return;
                }
                String reason = e.getArgs().replaceFirst("<(.*?)> ", "");
                Member target = C.getMentionedMember(e);

                if (target.getUser().getId().equalsIgnoreCase(e.getMember().getUser().getId())) {
                    e.replyError("You may not ban yourself! :wink:");
                    return;
                }

                C.privChannel(target, "Banned with Reason: " + reason);

                C.getCtrl(e).ban(target.getUser(), 7, "Banned by Moderator: " + e.getMember().getUser().getName()).reason("Banned by Moderator: " + e.getMember().getUser().getName() + ". With Reason: " + reason).queue();
                e.replySuccess("User " + target.getUser().getName() + "#" + target.getUser().getDiscriminator() + " has been **FRIGGING BANNED** by " + e.getMember().getEffectiveName());
            } else {
                e.replyError("**Correct Usage:** ^" + name + " " + arguments);
            }
        } else {
            e.replyError(C.permMsg(Roles.MODERATOR));
        }
    }
}
