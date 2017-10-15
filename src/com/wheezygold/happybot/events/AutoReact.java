package com.wheezygold.happybot.events;

        import com.wheezygold.happybot.util.C;
        import com.wheezygold.happybot.util.Channels;
        import com.wheezygold.happybot.util.Emotes;
        import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
        import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class AutoReact extends ListenerAdapter {


    public AutoReact() {
        C.log("AutoReact Loaded!");
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getChannel().getId().toString().equals(Channels.UPDATES.getId().toString())) {
            e.getMessage().addReaction(Emotes.getRandom().getEmote()).queue();
        }
    }

}
