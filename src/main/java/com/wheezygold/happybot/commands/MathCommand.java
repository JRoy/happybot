package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.udojava.evalex.Expression;
import com.wheezygold.happybot.util.C;

import java.math.BigDecimal;

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
            BigDecimal result = null;

            try {
                result = new Expression(e.getArgs()).eval();
            } catch (Expression.ExpressionException | ArithmeticException e1) {
                e.replyError("Invalid Expression!");
                return;
            }

            if (result == null) {
                e.replyError("Invalid Expression!");
                return;
            }

            e.replySuccess("**Expression Evaluated!**\n**Result:**" + C.codeblock(result.toPlainString()));
        } else {
            e.replyError("**Correct Usage:** ^" + name + " " + arguments);
        }



    }
}
