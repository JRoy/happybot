package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;
import io.github.jroy.happybot.util.RuntimeEditor;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class EvalCommand extends CommandBase {

    public EvalCommand() {
        super("eval", "<code>", "Evaluates Code!", CommandCategory.BOT, Roles.DEVELOPER);
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
        }
        ScriptEngine se = new ScriptEngineManager().getEngineByName("Nashorn");
        se.put("e", e);
        se.put("jda", e.getJDA());
        se.put("guild", e.getGuild());
        se.put("channel", e.getChannel());
        se.put("textchannel", e.getTextChannel());
        se.put("member", e.getMember());

        try {
            se.eval("var EmbedBuilder = Java.type(\"net.dv8tion.jda.core.EmbedBuilder\");");
            se.eval("var C = Java.type(\"io.github.jroy.happybot.util.C\");");
            se.eval("var Roles = Java.type(\"io.github.jroy.happybot.util.Roles\");");
            se.eval("var Channels = Java.type(\"io.github.jroy.happybot.util.Channels\");");
            se.eval("var Leveling = Java.type(\"io.github.jroy.happybot.levels.Leveling\");");
            e.reply("Evaluated Successfully:\n```\n" + se.eval(e.getArgs()) + " ```");
        } catch (Exception ex) {
            e.reply("An exception was thrown:\n```\n" + ex + " ```");
        }
    }

}
