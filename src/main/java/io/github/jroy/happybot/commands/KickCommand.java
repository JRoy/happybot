package io.github.jroy.happybot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.core.entities.Member;

public class KickCommand extends Command {
    public KickCommand() {
        this.name = "kick";
        this.help = "Kicks target user from server.";
        this.arguments = "<user mention> <reason>";
        this.guildOnly = true;
        this.category = new Category("Staff Tools");
        this.aliases = new String[]{"deport"};
    }

    @Override
    protected void execute(CommandEvent e) {
        if (C.hasRole(e.getMember(), Roles.HELPER)) {
            if (C.containsMention(e)) {
                if (e.getArgs().replaceAll("<(.*?)>", "").isEmpty()) {
                    e.replyError("**Correct Usage:** ^" + name + " " + arguments);
                    return;
                }
                String reason = e.getArgs().replaceFirst("<(.*?)> ", "");
                Member target = C.getMentionedMember(e);
                C.getCtrl(e).kick(target).reason("Kicked by Moderator: " + e.getMember().getUser().getName() + ". With Reason: " + reason).queue();
                e.replySuccess("User " + target.getUser().getName() + "#" + target.getUser().getDiscriminator() + " has been **FLIPPIN KICKED** by " + e.getMember().getEffectiveName());
            } else {
                e.replyError("**Correct Usage:** ^" + name + " " + arguments);
            }
        } else {
            e.replyError(C.permMsg(Roles.HELPER));
        }
    }
}
