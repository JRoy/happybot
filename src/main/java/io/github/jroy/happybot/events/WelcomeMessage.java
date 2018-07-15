package io.github.jroy.happybot.events;

import io.github.jroy.happybot.sql.MessageFactory;
import io.github.jroy.happybot.util.Channels;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class WelcomeMessage extends ListenerAdapter {

    private MessageFactory messageFactory;

    /**
     * Creates an WelcomeMessage Instance!
     */
    public WelcomeMessage(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        String joinformat = messageFactory.getRawMessage(MessageFactory.MessageType.JOIN).replaceAll("<player>", event.getMember().getAsMention()).replaceAll("<user>", event.getMember().getAsMention());
        if (!joinformat.startsWith("'s") || !joinformat.startsWith(","))
            joinformat = " " + joinformat;
        Channels.WELCOME.getChannel().sendMessage(event.getMember().getAsMention() + joinformat).queue();
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        String leaveformat = messageFactory.getRawMessage(MessageFactory.MessageType.LEAVE).replaceAll("<player>", "**"+ event.getMember().getUser().getName() + "**").replaceAll("<user>", "**"+ event.getMember().getUser().getName() + "**");
        if (!leaveformat.startsWith("'s") || !leaveformat.startsWith(","))
            leaveformat = " " + leaveformat;
        Channels.WELCOME.getChannel().sendMessage("**" + event.getMember().getUser().getName() + "**" + leaveformat).queue();
    }

}
