package com.wheezygold.happybot.events;

import com.wheezygold.happybot.util.*;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashSet;

public class StarMessages extends ListenerAdapter {

    private HashSet<String> alreadyUsedMessages = new HashSet<>();

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
        if (e.getReactionEmote().getName().equals("⭐")) {
            e.getChannel().getMessageById(e.getMessageId()).queue(this::handleStar);
        } else if (e.getReactionEmote().getName().equals("gild")) {
            if (!C.hasRole(e.getMember(), Roles.MODERATOR)) {
                e.getReaction().removeReaction().queue();
                return;
            }
            e.getChannel().getMessageById(e.getMessageId()).queue(message -> {
                if (!RuntimeEditor.isAllowSelfGilds()) {
                    if (e.getMember().getUser() == message.getAuthor()) {
                        e.getReaction().removeReaction().queue();
                        return;
                    }
                }
                handleGild(message);
            });
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
                .setDescription(message.getContentDisplay())
                .setFooter(footer, "http://icons.iconarchive.com/icons/paomedia/small-n-flat/1024/star-icon.png")
                .setThumbnail(message.getMember().getUser().getAvatarUrl())
                .setColor(message.getMember().getColor());
        if (C.containsImage(message))
            embed.setImage(C.getImage(message));
        Channels.STARRED_MESSAGES.getChannel().sendMessage(embed.build()).queue();
        C.privChannel(message.getMember(), privateMessageText);
        C.privChannel(message.getMember(), embed.build());
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
            if (message.getChannel().getId().equals(
                    Channels.STARRED_MESSAGES.getId()) || message.getChannel().getId().equals(
                    Channels.BOT_META.getId()) || message.getChannel().getId().equals(
                    Channels.TWITTER.getId()) || message.getChannel().getId().equals(Channels.LIVE.getId())) {
                return;
            }

            try {
                //noinspection ConstantConditions
                int numberOfStars = message.getReactions().stream()
                        .filter(reaction -> reaction.getReactionEmote().getName().equals("⭐"))
                        .findAny().orElse(null).getCount();

                if (numberOfStars == NUM_STARS_REQUIRED && !alreadyUsedMessages.contains(message.getId())) {
                    String footer = "New Starred Message from #" + message.getChannel().getName();
                    String privateMessageText = "Congrats! One of your messages has been starred:";
                    sendStarredMessage(footer, message, privateMessageText);
                }
            } catch (NullPointerException | IllegalStateException e) {
                Logger.error("Star reaction is in invalid state!");
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
                alreadyUsedMessages.add(message.getId());
            }

        }
    }
}
