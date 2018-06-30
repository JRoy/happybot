package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.core.entities.Member;

public class FansCommand extends CommandBase {

    public FansCommand() {
        super("fixfans", null, "Fixes broken users with no Fans Role", CommandCategory.STAFF, Roles.SUPER_ADMIN);
    }

    @Override
    protected void executeCommand(CommandEvent e) {
        e.getChannel().sendTyping().queue();
        int affected = 0;
        for (Member curM : C.getGuild().getMembers()) {
            if (!C.hasRole(curM, Roles.FANS) && !curM.getUser().isBot()) {
                C.giveRole(curM, Roles.FANS);
                affected++;
            }
        }
        e.replySuccess("All Done!\n" + affected + " Users Affected!");
    }
}
