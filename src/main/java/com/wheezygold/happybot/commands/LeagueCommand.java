package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.apis.League;
import net.dv8tion.jda.core.EmbedBuilder;
import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.constant.Platform;

import java.util.Arrays;
import java.util.HashMap;
import java.util.NoSuchElementException;

public class LeagueCommand extends Command {

    private final League league;

    public LeagueCommand(League league) {
        this.league = league;
        this.name = "league";
        this.help = "Shows league stats for target.";
        this.arguments = "<" + Arrays.toString(Platform.values()).replaceAll("[1]", "") + "> <league username>";
        this.guildOnly = false;
        this.category = new Category("Fun");
    }

    @Override
    protected void execute(CommandEvent e) {
        if (e.getArgs().isEmpty()) {
            e.replyError("**Correct Usage:** ^" + name + " " + arguments);
            return;
        }
        String[] args = e.getArgs().split("[ ]");
        if (args.length != 2) {
            e.replyError("**Correct Usage:** ^" + name + " " + arguments);
            return;
        }
        Platform platform;
        try {
            platform = Platform.getPlatformByName(args[0]);
        } catch (NoSuchElementException e1) {
            e.replyError("Invalid platform!");
            return;
        }
        try {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("LoL Player Statistics")
                    .setDescription("Listing statistics:")
                    .setFooter("Stats provided by Riot Games's API", "http://i.imgur.com/xNLs83T.png");
            for (HashMap.Entry<String, String> entry : league.getAllFields(args[1], platform).entrySet()) {
                if (entry.getValue() != null && !entry.getValue().equals("0")) {
                    embed.addField("**" + entry.getKey() + "**", entry.getValue(), true);
                }
            }
            e.reply(embed.build());
        } catch (RiotApiException | IllegalArgumentException e1) {
            e.replyError("You have entered an invalid username or you are not in the selected platform!");
        }
    }

}
