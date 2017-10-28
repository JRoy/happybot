package com.wheezygold.happybot.events;

import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Channels;
import com.wheezygold.happybot.util.Roles;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashSet;

public class StarMessages extends ListenerAdapter {

    private HashSet<String> alreadyUsedMessages = new HashSet<>();

    public StarMessages() {
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
        if (e.getReactionEmote().getName().equals("star")) {
            e.getChannel().getMessageById(e.getMessageId()).queue(this::handleStar);
        } else if (e.getReactionEmote().getName().equals("gild")) {
            if (!C.hasRole(e.getMember(), Roles.MODERATOR)) {
                e.getReaction().removeReaction().queue();
                return;
            }
            //huh
            e.getChannel().getMessageById(e.getMessageId()).queue(this::handleGild);
        }
    }

    private void handleStar(Message message) {
        HandleStar handleStar = new HandleStar(message);
        Thread t = new Thread(handleStar);
        t.start();
    }

    private void handleGild(Message message) {
        HandleGild handleGild = new HandleGild(message);
        Thread t = new Thread(handleGild);
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
        alreadyUsedMessages.add(message.getId());
    }

    private class HandleStar implements Runnable {

        private Message message;
        private static final int NUM_STARS_REQUIRED = 5;

        HandleStar(Message message) {
            this.message = message;
        }

        @Override
        public void run() {
            if (message.getChannel().getId().equals("369214529847951361") || message.getChannel().getId().equals("360544824434098188") || message.getChannel().getId().equals("362333614580432896") || message.getChannel().getId().equals("294588669682122752"))
                return;
            int numberOfStars = 0;
            for (MessageReaction reaction : message.getReactions()) {
                if (reaction.getEmote().getName().equals("star")) numberOfStars++;
            }
            if (numberOfStars == NUM_STARS_REQUIRED && !alreadyUsedMessages.contains(message.getId())) {
                String footer = "New Starred Message from #" + message.getChannel().getName();
                String privateMessageText = "Congrats! One of your messages has been stared:";
                sendStarredMessage(footer, message, privateMessageText);
            }
        }

    }

    private class HandleGild implements Runnable {

        private Message message;

        HandleGild(Message message) {
            this.message = message;
        }

        @Override
        public void run() {
            if (!alreadyUsedMessages.contains(message.getId())) {
                String footer = "New Gilded Message from #" + message.getChannel().getName();
                String privateMessageText = "Congrats! One of your messages has been gilded by a staff member:";
                sendStarredMessage(footer, message, privateMessageText);
            }

        }
    }
}
