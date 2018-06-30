package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.apis.League;
import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.C;
import net.dv8tion.jda.core.EmbedBuilder;
import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.constant.Platform;

import java.util.Arrays;
import java.util.HashMap;
import java.util.NoSuchElementException;

public class LeagueCommand extends CommandBase {

    private final League league;

    public LeagueCommand(League league) {
        super("league", "<" + Arrays.toString(Platform.values()).replaceAll("[1]", "") + "> <league username>", "Shows the league of legend stats of the target user!", CommandCategory.FUN);
        this.league = league;
    }

    @Override
    protected void executeCommand(CommandEvent e) {
        if (e.getArgs().isEmpty()) {
            e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
            return;
        }
        String[] args = e.getSplitArgs();
        if (args.length != 2) {
            e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
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
