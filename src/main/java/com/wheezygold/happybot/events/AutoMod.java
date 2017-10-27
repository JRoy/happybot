package com.wheezygold.happybot.events;

import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Roles;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class AutoMod extends ListenerAdapter {
    private final String owner;

    /**
     * Creates an AutoMod Instance!
     *
     * @param owner The ID of the bot owner!
     */
    public AutoMod(String owner) {
        this.owner = owner;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        checkForAdvertising(event.getGuild(), event.getMember(), event.getMessage(), event.getChannel(), event.getJDA());
    }

    @Override
    public void onGuildMessageUpdate(GuildMessageUpdateEvent event) {
        checkForAdvertising(event.getGuild(), event.getMember(), event.getMessage(), event.getChannel(), event.getJDA());
    }

    private void checkForAdvertising(Guild guild, Member member, Message message, TextChannel channel, JDA jda) {
        if (C.hasRole(member, Roles.SUPER_ADMIN) || C.hasRole(member, Roles.BOT))
            return;
        if (!message.getContent().toLowerCase().contains("discord.gg/"))
            return;
        message.delete().reason("Advertising Link with Message: " + message.getStrippedContent()).queue();
        channel.sendMessage(member.getAsMention() + "! Do not advert other discord servers!").queue();
        jda.getTextChannelById("318456047993880577").sendMessage(member.getAsMention() + " attempted to advert the following link: " + message.getContent()).queue();
    }

}
