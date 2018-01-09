package com.wheezygold.happybot.events;

import com.wheezygold.happybot.Main;
import com.wheezygold.happybot.util.*;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.StatusChangeEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class AutoMod extends ListenerAdapter {

    private List<Message> processedMessages = new ArrayList<>();
    private MessageFactory messageFactory;
    private final Pattern pattern;

    public AutoMod(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
        pattern = Pattern.compile("(?:https?://)?discord(?:app\\.com/invite|\\.gg)/(\\S+)", Pattern.CASE_INSENSITIVE);
    }

    @Override
    public void onStatusChange(StatusChangeEvent event) {
        if (event.getStatus() == JDA.Status.CONNECTED) {
            Channels.BOT_META.getChannel().getHistory().retrievePast(10).queue(messages -> messages.forEach(message -> {
                message.getEmbeds().forEach(messageEmbed -> {
                    if (messageEmbed != null && messageEmbed.getTitle().equalsIgnoreCase("Impending Update") && message.getAuthor() == Main.getJda().getUserById(Constants.BOT_ID.get()) && !message.isWebhookMessage()) {
                        message.editMessage(new EmbedBuilder()
                                .setTitle("Update Complete")
                                .setDescription(messageFactory.getRawMessage(MessageFactory.MessageType.UPDATE_END) + "\nThis update has been finished in PID: " + ManagementFactory.getRuntimeMXBean().getName().split("[@]")[0])
                                .build()).queue();
                    }
                });
            }));
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        checkForAdvertising(event.getMember(), event.getMessage(), event.getChannel());
        if (message.getChannel() == Channels.BOT_META.getChannel() && message.isWebhookMessage()) {
            MessageEmbed embed = message.getEmbeds().get(0);
            if ((embed.getTitle().startsWith("[WheezyGold7931/happybot] Issue closed:") || embed.getTitle().startsWith("[WheezyGold7931/happybot] New comment on issue") ) && !RuntimeEditor.isPingIssueClose())
                return;
            Roles.GIT.getRole().getManager().setMentionable(true).queue();
            Channels.BOT_META.getChannel().sendMessage(Roles.GIT.getRole().getAsMention()).queue();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                Roles.GIT.getRole().getManager().setMentionable(false).queue();
            }
        }
            
    }

    @Override
    public void onGuildMessageUpdate(GuildMessageUpdateEvent event) {
        if (RuntimeEditor.isFilteringAdverts())
            checkForAdvertising(event.getMember(), event.getMessage(), event.getChannel());
    }

    private void checkForAdvertising(Member member, Message message, TextChannel channel) {
        if (C.hasRole(member, Roles.SUPER_ADMIN) || C.hasRole(member, Roles.BOT))
            return;
        if (!pattern.matcher(message.getContentRaw()).matches())
            return;
        message.delete().reason("Advertising Link with Message: " + message.getContentStripped()).queue();
        Channels.LOG.getChannel().sendMessage(member.getAsMention() + " attempted to advert the following link: " + message.getContentRaw()).queue();
        C.privChannel(member, "You cannot advertise in the happyheart guild!");
        if (!processedMessages.contains(message)) {
            channel.sendMessage(member.getAsMention() + "! Do not advert other discord servers!").queue();
            processedMessages.add(message);
        }
    }

}
