package com.wheezygold.happybot.events;

import com.wheezygold.happybot.util.Channels;
import com.wheezygold.happybot.util.Emotes;
import com.wheezygold.happybot.util.Logger;
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
        } else if (e.getChannel().getId().equals(Channels.STAFF_ANNOUNCEMENTS)) {
            e.getMessage().addReaction(Emotes.getRandom().getEmote()).queue();
        }
    }

}
