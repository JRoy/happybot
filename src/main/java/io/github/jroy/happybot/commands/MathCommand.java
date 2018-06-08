package io.github.jroy.happybot.commands;

import com.udojava.evalex.Expression;
import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.C;

public class MathCommand extends CommandBase {

    public MathCommand() {
        super("math", "<math>", "Evaluates a math expression!", CommandCategory.FUN);
        this.aliases = new String[] { "maths" };
    }

    @Override
    protected void executeCommand(CommandEvent e) {
        if (!e.getArgs().isEmpty()) {
            String result;

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
