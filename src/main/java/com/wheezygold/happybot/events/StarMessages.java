package com.wheezygold.happybot.events;

import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Channels;
import com.wheezygold.happybot.util.Roles;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashMap;

public class StarMessages extends ListenerAdapter {

    private HashMap<String, Message> used;

    public StarMessages() {
        used = new HashMap<>();
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
        if (e.getReactionEmote().getName().equals("⭐")) {
            e.getChannel().getMessageById(e.getMessageId()).queue(this::handleStar);
        } else if (e.getReactionEmote().getName().equals("gild")) {
            if (!C.hasRole(e.getMember(), Roles.MODERATOR)) {
                e.getReaction().removeReaction().queue();
                return;
            }
            //huh
            e.getChannel().getMessageById(e.getMessageId()).queue(this::handleGuild);
        }
    }

    private void handleStar(Message message) {
        HandleStar handleStar = new HandleStar(message);
        Thread t = new Thread(handleStar);
        t.start();
    }

    private void handleGuild(Message message) {
        HandleGuild handleGuild = new HandleGuild(message);
        Thread t = new Thread(handleGuild);
        t.start();
    }

    private void sendStarredMessage(String footer, Message message, String privateMessageText) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(message.getMember().getEffectiveName())
                .setDescription(message.getStrippedContent())
                .setFooter(footer, "https://google.com")
                .setThumbnail(message.getMember().getUser().getAvatarUrl())
                .setColor(message.getMember().getColor());
        Channels.STARRED_MESSAGES.getChannel().sendMessage(embed.build()).queue();
        message.getAuthor().openPrivateChannel().queue(pc -> pc.sendMessage(privateMessageText).queue());
        message.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(embed.build()).queue());
        used.put(message.getId(), message);
    }

    private class HandleStar implements Runnable {

        private Message message;

        public HandleStar(Message message) {
            this.message = message;
        }

        @Override
        public void run() {
            if (message.getChannel().getId().equals("369214529847951361") || message.getChannel().getId().equals("360544824434098188") || message.getChannel().getId().equals("362333614580432896") || message.getChannel().getId().equals("294588669682122752"))
                return;
            int help = 0;
            for (User u : message.getReactions().stream().filter(r -> r.getEmote().getName().equals("⭐")).findAny().orElse(null).getUsers()) {
                help++;
            }
            if (help == 5) {
                if (!used.containsKey(message.getId())) {
                    String footer = "New Starred Message from #" + message.getChannel().getName();
                    String privateMessageText = "Congrats! One of your messages has been stared:";
                    sendStarredMessage(footer, message, privateMessageText);
                }
            }
        }

    }

    private class HandleGuild implements Runnable {

        private Message message;

        public HandleGuild(Message message) {
            this.message = message;
        }

        @Override
        public void run() {
            if (!used.containsKey(message.getId())) {
                String footer = "New Gilded Message from #" + message.getChannel().getName();
                String privateMessageText = "Congrats! One of your messages has been gilded by a staff member:";
                sendStarredMessage(footer, message, privateMessageText);
            }

        }
    }
}
