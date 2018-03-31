package io.github.jroy.happybot.events;

import io.github.jroy.happybot.util.Channels;
import io.github.jroy.happybot.util.Emotes;
import io.github.jroy.happybot.util.Logger;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class AutoReact extends ListenerAdapter {


    public AutoReact() {
        Logger.info("AutoReact Loaded!");
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getChannel().getId().equals(Channels.UPDATES.getId())) {
            e.getMessage().addReaction(Emotes.getRandom().getEmote()).queue();
        } else if (e.getChannel().getId().equals(Channels.STAFF_ANNOUNCEMENTS.getId())) {
            e.getMessage().addReaction(Emotes.getRandom().getEmote()).queue();
        }
    }

}
