package io.github.jroy.happybot.commands.base;

import com.jagrosh.jdautilities.command.CommandClient;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandEvent extends com.jagrosh.jdautilities.command.CommandEvent {

    public CommandEvent(MessageReceivedEvent event, String args, CommandClient client) {
        super(event, args, client);
    }

    public boolean hasRole(Roles role) {
        return C.hasRole(getMember(), role);
    }

    public boolean containsMention() {
        return C.containsMention(this);
    }

    public Member getMentionedMember() {
        return C.getMentionedMember(this);
    }

    public String[] getSplitArgs() {
        return getArgs().split("[ ]");
    }

}
