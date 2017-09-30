package com.wheezygold.happybot.util;

import com.jagrosh.jdautilities.commandclient.CommandEvent;

public interface CommandHelper {

    public static void displayHelp(CommandEvent commandEvent) {
        commandEvent.replyError("f");
    }

}
