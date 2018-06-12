package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;
import io.github.jroy.happybot.util.RuntimeEditor;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class EvalCommand extends CommandBase {

    public EvalCommand() {
        super("eval", "<code>", "Evaluates Code!", CommandCategory.BOT);
        this.name = "eval";
        this.help = "Evaluates Code!";
        this.arguments = "<java code>";
        this.guildOnly = false;
        this.category = new Category("Bot Management");
    }

    @Override
    protected void executeCommand(CommandEvent e) {
        if (RuntimeEditor.isEvalOwnerOnly()) {
            if (!e.isOwner()) {
                e.replyError(C.permMsg(Roles.DEVELOPER));
                return;
            }
        } else {
            if (e.hasRole(Roles.DEVELOPER)) {
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
                    "C," +
                    "Roles," +
                    "Channels," +
                    "RuntimeEditor," +
                    "net.dv8tion.jda.core.entities.Game," +
                    "Packages.com.wheezygold.happybot.util," +
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
        se.put("textchannel", e.getTextChannel());
        se.put("member", e.getMember());
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
