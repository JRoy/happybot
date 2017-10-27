package com.wheezygold.happybot.embed;

import com.wheezygold.happybot.Main;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.awt.*;

/**
 * Created by Mike M on 6/28/2017.
 */
@SuppressWarnings("unused")
public class Embed {

    public static Message generate(String title, String thumbnail, String[] body, Color color) {
        StringBuilder sb = new StringBuilder();
        for (String input : body) {
            sb.append(input + "\n");
        }
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(title);
        eb.setDescription(sb.toString());
        eb.setFooter("API v1", Main.getJda().getSelfUser().getAvatarUrl());
        eb.setThumbnail(thumbnail)
                .setColor(color);
        MessageEmbed embed = eb.build();
        MessageBuilder mb = new MessageBuilder();
        mb.setEmbed(embed);
        return mb.build();
    }

    public static Message generate(String title, String thumbnail, String head, String body, Color color) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(title);
        eb.setDescription(head + "\n\n" + body + "\n");
        eb.setFooter("API v1", Main.getJda().getSelfUser().getAvatarUrl());
        eb.setThumbnail(thumbnail)
                .setColor(color);
        MessageEmbed embed = eb.build();
        MessageBuilder mb = new MessageBuilder();
        mb.setEmbed(embed);
        return mb.build();
    }

    public static Message generate(String title, String thumbnail, String head, String body, String field, String fieldValue, Color color) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(title);
        eb.setDescription(head + "\n\n" + body + "\n");
        eb.setFooter("API v1", Main.getJda().getSelfUser().getAvatarUrl());
        eb.setThumbnail(thumbnail)
                .setColor(color);
        eb.addField(field, fieldValue, true);
        MessageEmbed embed = eb.build();
        MessageBuilder mb = new MessageBuilder();
        mb.setEmbed(embed);
        return mb.build();
    }

    public static Message generate(String title, String thumbnail, String head, String body, String field[], String fieldValue[], Color color) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(title);
        eb.setDescription(head + "\n\n" + body + "\n");
        eb.setFooter("API v1", Main.getJda().getSelfUser().getAvatarUrl());
        eb.setThumbnail(thumbnail)
                .setColor(color);
        for (String fieldz : field) {
            for (String val : fieldValue) {
                eb.addField(fieldz, val, true);
            }
        }
        MessageEmbed embed = eb.build();
        MessageBuilder mb = new MessageBuilder();
        mb.setEmbed(embed);
        return mb.build();
    }

    public static EmbedBuilder generateBuilder(String title, String thumbnail, String head, String body, Color color) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(title);
        eb.setDescription(head + "\n\n" + body + "\n");
        eb.setFooter("API v1", Main.getJda().getSelfUser().getAvatarUrl());
        eb.setThumbnail(thumbnail)
                .setColor(color);
        MessageEmbed embed = eb.build();
        MessageBuilder mb = new MessageBuilder();
        mb.setEmbed(embed);
        return eb;
    }

    public static String generateHelpLine(Emoticon rank, String cmd, String desc) {
        return rank.getTranslation() + " **" + cmd + "** " + desc;
    }

}
