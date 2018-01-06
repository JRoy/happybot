package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Channels;
import com.wheezygold.happybot.util.Roles;
import com.wheezygold.happybot.util.RuntimeEditor;
import net.dv8tion.jda.core.entities.Game;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class EvalCommand extends Command {

    private C CClass = new C();


    public EvalCommand() {
        this.name = "eval";
        this.help = "Evaluates Code!";
        this.arguments = "<java code>";
        this.guildOnly = false;
        this.category = new Category("Bot Management");
    }

    @Override
    protected void execute(CommandEvent e) {
        if (RuntimeEditor.isEvalOwnerOnly()) {
            if (!e.isOwner()) {
                e.replyError(C.permMsg(Roles.DEVELOPER));
                return;
            }
        } else {
            if (!C.hasRole(e.getMember(), Roles.DEVELOPER)) {
                e.replyError(C.permMsg(Roles.DEVELOPER));
                return;
            }
        }
        ScriptEngine se = new ScriptEngineManager().getEngineByName("Nashorn");
        try {
            se.eval("var imports = new JavaImporter(" +
                    "java.io," +
                    "java.lang," +
                    "java.util," +
                    "com.wheezygold.happybot.util.C," +
                    "Packages.net.dv8tion.jda.core," +
                    "Packages.net.dv8tion.jda.core.entities," +
                    "Packages.net.dv8tion.jda.core.entities.impl," +
                    "Packages.net.dv8tion.jda.core.managers," +
                    "Packages.net.dv8tion.jda.core.managers.impl," +
                    "Packages.net.dv8tion.jda.core.utils);");
        } catch (ScriptException e1) {
            e1.printStackTrace();
        }
        se.put("e", e);
        se.put("jda", e.getJDA());
        se.put("guild", e.getGuild());
        se.put("channel", e.getChannel());
        se.put("member", e.getMember());
        se.put("Roles", Roles.class);
        se.put("Channels", Channels.class);
        se.put("Game", Game.class);
        try {
            e.reply(e.getClient().getSuccess() + " Evaluated Successfully:\n```\n" + se.eval("(function() {" +
                    "with (imports) {" +
                    e.getArgs() +
                    "}" +
                    "})();") + " ```");
        } catch (Exception ex) {
            e.reply(e.getClient().getError() + " An exception was thrown:\n```\n" + ex + " ```");
        }
    }

}
