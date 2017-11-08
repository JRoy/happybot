package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.udojava.evalex.Expression;
import com.wheezygold.happybot.util.C;

public class MathCommand extends Command {
    public MathCommand() {
        this.name = "math";
        this.help = "Evaluates math!";
        this.arguments = "<math>";
        this.category = new Category("Fun");
    }

    @Override
    protected void execute(CommandEvent e) {

        if (!e.getArgs().isEmpty()) {
            String  result = null;

            if (e.getArgs().equalsIgnoreCase("quick maths")) {
                result = "2 + 2 - 1 = that's 3 quick maths.";
                e.replySuccess("**Expression Evaluated!**\n**Result:**" + C.codeblock(result));
                return;
            }

            if (e.getArgs().replace(" ", "").equalsIgnoreCase("2+2-1")) {
                result = "that's 3 quick maths";
                e.replySuccess("**Expression Evaluated!**\n**Result:**" + C.codeblock(result));
                return;
            }
            
            if (e.getArgs().replace(" ", "").replace("π", "pi").equalsIgnoreCase("e^pi^i")) {
                result = "-1";
                e.replySuccess("**Expression Evaluated!**\n**Result:**" + C.codeblock(result));
                return;
            }

            try {
                result = new Expression(e.getArgs()).eval().toPlainString();
            } catch (Expression.ExpressionException | ArithmeticException e1) {
                e.replyError("Invalid Expression!");
                return;
            }

            if (result == null) {
                e.replyError("Invalid Expression!");
                return;
            }

            e.replySuccess("**Expression Evaluated!**\n**Result:**" + C.codeblock(result));
        } else {
            e.replyError("**Correct Usage:** ^" + name + " " + arguments);
        }



    }
}
