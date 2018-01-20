package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.apis.League;
import net.dv8tion.jda.core.EmbedBuilder;
import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.constant.Platform;

import java.util.HashMap;

public class LeagueCommand extends Command {

    private final League league;

    public LeagueCommand(League league) {
        this.league = league;
        this.name = "league";
        this.help = "Shows league stats for target.";
        this.arguments = "<league username>";
        this.guildOnly = false;
        this.category = new Category("Fun");
    }

    @Override
    protected void execute(CommandEvent e) {
        if (e.getArgs().isEmpty()) {
            e.replyError("**Correct Usage:** ^" + name + " " + arguments);
            return;
        }
        try {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("LoL Player Statistics")
                    .setDescription("Listing statistics:")
                    .setFooter("Stats provided by Riot Games's API", "http://i.imgur.com/xNLs83T.png");
            for (HashMap.Entry<String, String> entry : league.getAllFields(e.getArgs(), Platform.NA).entrySet()) {
                if (entry.getValue() != null && !entry.getValue().equals("0")) {
                    embed.addField("**" + entry.getKey() + "**", entry.getValue(), true);
                }
            }
            e.reply(embed.build());
        } catch (RiotApiException | IllegalArgumentException e1) {
            e.replyError("You have entered an invalid username or you are not in the NA platform!");
        }
    }

}
