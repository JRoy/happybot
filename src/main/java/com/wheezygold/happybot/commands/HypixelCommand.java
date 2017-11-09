package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;

import com.kbrewster.exceptions.APIException;
import com.kbrewster.hypixelapi.player.HypixelPlayer;
import com.wheezygold.happybot.util.Hypixel;
import net.dv8tion.jda.core.EmbedBuilder;

import java.util.HashMap;

public class HypixelCommand extends Command {

    private Hypixel hypixel;

    public HypixelCommand(Hypixel hypixel) {
        this.name = "hypixel";
        this.help = "Check your hypixel stats!";
        this.arguments = "<player>";
        this.category = new Category("Fun");
        this.hypixel = hypixel;
    }

    @Override
    protected void execute(CommandEvent e) {
        if (!e.getArgs().isEmpty()) {
            if (hypixel.isValidPlayer(e.getArgs())) {
                try {
                    HypixelPlayer player = hypixel.getPlayer(e.getArgs());
                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("Hypixel Player Statistics")
                            .setDescription("Listing statistics:")
                            .setFooter("Stats provided by Hypixel's API", "https://media-curse.cursecdn.com/attachments/264/727/f7c76fdb4569546a9ddf0e58c8653823.png");
                    for (HashMap.Entry<String, String> entry : Hypixel.getAllFields(player).entrySet()) {
                        if (entry.getValue() != null) {
                            if (!entry.getValue().equals("0")) {
                                embed.addField("**" + entry.getKey() + "**", entry.getValue(), true);
                            }
                        }
                    }
                    e.reply(embed.build());
                } catch (APIException e1) {
                    e.replyError("The API had an error or the player was invalid.");
                }
            } else {
                e.replyError("The API had an error or the player was invalid.");
            }
        } else {
            e.replyError("**Correct Usage:** ^" + name + " " + arguments);
        }
    }
}
