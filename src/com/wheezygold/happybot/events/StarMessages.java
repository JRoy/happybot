package com.wheezygold.happybot.events;

import com.wheezygold.happybot.util.Channels;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class StarMessages extends ListenerAdapter {

    public StarMessages() {
        //lol
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
        if (e.getReactionEmote().getName().equals("⭐")) {

            e.getChannel().getMessageById(e.getMessageId()).queue(m -> handleStar(m));
        }
    }

    private void handleStar(Message message) {
        HandleStar handleStar = new HandleStar(message);
        Thread t = new Thread(handleStar);
        t.start();
    }

    private class HandleStar implements Runnable {

        private Message message;

        public HandleStar(Message message) {
            this.message = message;
        }

        @Override
        public void run() {
            int help = 0;
            for (User u : message.getReactions().stream().filter(r -> r.getEmote().getName().equals("⭐")).findAny().orElse(null).getUsers()) {
                help++;
            }
            if (help == 5) {
                String footer = "New Stared Message from #" + message.getChannel().getName();
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle(message.getMember().getEffectiveName())
                        .setDescription(message.getStrippedContent())
                        .setFooter(footer, "https://google.com")
                        .setThumbnail(message.getMember().getUser().getAvatarUrl())
                        .setColor(message.getMember().getColor());
                Channels.STARED_MESSAGES.getChannel().sendMessage(embed.build()).queue();
                message.getAuthor().openPrivateChannel().queue(pc -> pc.sendMessage("Congrats! One of your messages has been started:").queue());
                message.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(embed.build()).queue());
            }
        }

    }
}
