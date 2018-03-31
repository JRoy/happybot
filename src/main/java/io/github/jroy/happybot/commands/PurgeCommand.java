package io.github.jroy.happybot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;

public class PurgeCommand extends Command {

    public PurgeCommand() {
        this.name = "purge";
        this.arguments = "<amount> [<user>]";
        this.help = "Purges target messages.";
        this.guildOnly = true;
        this.category = new Category("Staff Tools");
        this.aliases = new String[]{"clear"};
    }

    @Override
    protected void execute(CommandEvent e) {
        if (C.hasRole(e.getMember(), Roles.HELPER)) {
            String[] args = e.getArgs().split("[ ]");
            int target = 0;
            if (args.length == 1 || args.length == 2) {
                if (!StringUtils.isNumeric(args[0])) {
                    e.replyError("**Correct Usage:** ^" + name + " " + arguments);
                    return;
                } else {
                    target = Integer.parseInt(args[0]);
                    if (target > 100) {
                        e.replyWarning(":warning: Target amount is over 100! Lowering target to 100.");
                        target = 100;
                    }
                }
                if (args.length == 2) {
                    if (!C.containsMention(e)) {
                        e.replyError("**Correct Usage:** ^" + name + " " + arguments);
                        return;
                    }
                }
            }
            MessageHistory history = e.getChannel().getHistory();
            switch (args.length) {
                case 2: { /* 2 Arguments: Purging Target User's Messages in Target Amount */
                    List<Message> del = new LinkedList<>();
                    List<Message> targets = history.retrievePast(target).complete();
                    for (Message msgz : targets) {
                        if (msgz.getAuthor().getId().equals(C.getMentionedMember(e).getUser().getId()))
                            del.add(msgz);
                    }
                    e.getTextChannel().deleteMessages(del).complete();
                    break;
                }
                case 1: { /* 1 Argument: Purging ALL Messages in Target Amount */
                    List<Message> del = new LinkedList<>();
                    del.addAll(history.retrievePast(target).complete());
                    e.getTextChannel().deleteMessages(del).complete();
                    break;
                }
                default: { /* No Arguments or Too Many Arguments */
                    e.replyError("**Correct Usage:** ^" + name + " " + arguments);
                    return;
                }
            }
            e.replySuccess("Deleted Messages!");
        } else {
            e.replyError(C.permMsg(Roles.HELPER));
        }
    }

}
