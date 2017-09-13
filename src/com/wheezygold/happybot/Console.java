package com.wheezygold.happybot;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Console {

    private static ScriptEngine engine;
    private Scanner scanner;

    public Console() {

        engine = new ScriptEngineManager().getEngineByName("nashorn");
        try {
            engine.eval("var imports = new JavaImporter(" +
                    "java.io," +
                    "java.lang," +
                    "java.util," +
                    "com.wheezygold.happybot.Main," +
                    "Packages.net.dv8tion.jda.core," +
                    "Packages.net.dv8tion.jda.core.entities," +
                    "Packages.net.dv8tion.jda.core.entities.impl," +
                    "Packages.net.dv8tion.jda.core.managers," +
                    "Packages.net.dv8tion.jda.core.managers.impl," +
                    "Packages.com.jagrosh.jdautilities.commandclient," +
                    "Packages.com.wheezygold.happybot.commands," +
                    "Packages.net.dv8tion.jda.core.utils);");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        while(true) {
            try {
                scanner = new Scanner(System.in);
            } catch (NoSuchElementException e) {
                //lololol
            }

            try {
                String excmd = scanner.nextLine();
                ScriptEngineManager manager = new ScriptEngineManager();
//                ScriptEngine engine = manager.getEngineByName("nashorn");
//                Object result = engine.eval(cmd);
                Object out = engine.eval(
                        "(function() {" +
                                "with (imports) {" +
                                excmd +
                                "}" +
                                "})();");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String eval(String cmd) {
        try {
            Object out = engine.eval(
                    "(function() {" +
                            "with (imports) {" +
                            cmd +
                            "}" +
                            "})();");
            return out.toString();
        } catch (Exception e) {
            return e.getMessage();
        }
     }
}
