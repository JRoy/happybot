package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;

public class FanartCommand extends CommandBase {

    public FanartCommand() {
        super("fanart", "<user>", "Puts a no-fanart message to a player!", CommandCategory.STAFF, Roles.HELPER);
    }

    @Override
    protected void executeCommand(CommandEvent e) {
        if (e.getMessage().getMentionedUsers().size() == 1) {
            e.getMessage().delete().reason("Auto Command Deletion").queue();
            String msg = C.getMentionedMember(e).getAsMention() + ", please post only fanart in " + e.getGuild().getTextChannelById("337689640888827905").getAsMention();
            e.replySuccess(msg);
            C.privChannel(C.getMentionedMember(e), msg);
        } else {
            e.replyError("**Correct Usage:** ^" + name + " " + arguments);
        }
    }
}
