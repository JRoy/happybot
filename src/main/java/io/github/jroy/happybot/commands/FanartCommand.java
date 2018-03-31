package io.github.jroy.happybot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;

public class FanartCommand extends Command {

    public FanartCommand() {
        this.name = "fanart";
        this.help = "Puts a no-fanart message to a player!";
        this.arguments = "<user>";
        this.guildOnly = true;
        this.category = new Category("Staff Tools");
    }

    @Override
    protected void execute(CommandEvent e) {
        if (C.hasRole(e.getMember(), Roles.HELPER)) {
            if (e.getMessage().getMentionedUsers().size() == 1) {
                e.getMessage().delete().reason("Auto Command Deletion").queue();
                String msg = C.getMentionedMember(e).getAsMention() + ", please post only fanart in " + e.getGuild().getTextChannelById("337689640888827905").getAsMention();
                e.replySuccess(msg);
                C.privChannel(C.getMentionedMember(e), msg);
            } else {
                e.replyError("**Correct Usage:** ^" + name + " " + arguments);
            }
        } else {
            e.replyError(C.permMsg(Roles.HELPER));
        }
    }
}
