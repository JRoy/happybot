package com.wheezygold.happybot;

import com.wheezygold.happybot.util.C;

import javax.script.ScriptEngine;
import java.util.NoSuchElementException;
import java.util.Scanner;

class Console {

    private static ScriptEngine engine;
    private Scanner scanner;

    Console() {

        while(true) {
            try {
                scanner = new Scanner(System.in);
            } catch (NoSuchElementException e) {
                //When we shutdown the bot it makes a dum stack trace.
                //I do not care about it.
            }
            switch (scanner.nextLine()) {
                case "instance": {
                    Main.getJda().shutdown();
                    C.log("The JDA instance has been shutdown!");
                    break;
                }
                case "vm": {
                    try {
                        Main.getJda().shutdown();
                        C.log("JDA was Shutdown!");
                    } catch (Exception e1) {
                        C.log("JDA was already shutdown, exiting VM...");
                        //JUST IN CASE WE STOPPED IT
                    }
                    C.log("Exiting the Java VM!");
                    System.exit(0);
                    break;
                }
                default: C.log("Invalid Command"); break;
            }
        }
    }
}
