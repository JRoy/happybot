package com.wheezygold.happybot.util;

import com.wheezygold.happybot.Main;
import net.dv8tion.jda.core.entities.Icon;

import java.io.File;
import java.io.IOException;

/**
 * The Manager of the Theme System.
 * Changes Icon, Guild Name, Role Names, and Bot Nickname.
 */
public class Theme {

    /**
     * Opens a a new thread and switches to the normal theme.
     */
    public static void toNormal() {
        NormalTheme normalTheme = new NormalTheme();
        Thread t = new Thread(normalTheme);
        t.start();
    }

    /**
     * Opens a a new thread and switches to the spooky theme.
     */
    public static void toSpooky() {
        SpookyTheme spookyTheme = new SpookyTheme();
        Thread t = new Thread(spookyTheme);
        t.start();
    }

    /**
     * Opens a a new thread and switches to the winter theme.
     */
    public static void toWinter() {
        WinterTheme winterTheme = new WinterTheme();
        Thread t = new Thread(winterTheme);
        t.start();
    }

    /**
     * The runnable that the thread used for normal theme.
     */
    private static class NormalTheme implements Runnable {

        public NormalTheme() {
        }

        @Override
        public void run() {
            C.getGuild().getManager().setName("Happyheart Fanbase").queue();
            try {
                Icon icon = Icon.from(new File("normal.png"));
                C.getGuild().getManager().setIcon(icon).queue();
                for (Roles crole : Roles.values()) {
                    crole.getRole().getManager().setName(crole.getRoleName()).queue();
                }
                C.getGuild().getManager().setName("Happyheart Fanbase").complete();
                C.getGuildCtrl().setNickname(C.getGuild().getMemberById("354736186516045835"), "Happy Bot").complete();
                Main.updateTheme();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * The runnable that the thread used for spooky theme.
     */
    private static class SpookyTheme implements Runnable {

        public SpookyTheme() {
        }

        @Override
        public void run() {
            C.getGuild().getManager().setName("Happyheart Spookbase").queue();
            try {
                Icon icon = Icon.from(new File("spooky.png"));
                C.getGuild().getManager().setIcon(icon).queue();
                for (Roles crole : Roles.values()) {
                    crole.getRole().getManager().setName(crole.getSpook()).queue();
                }
                C.getGuild().getManager().setName("Happyheart Spookbase").complete();
                C.getGuildCtrl().setNickname(C.getGuild().getMemberById("354736186516045835"), "Spooky Bot").complete();
                Main.updateTheme();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * The runnable that the thread used for spooky theme.
     */
    private static class WinterTheme implements Runnable {

        public WinterTheme() {
        }

        @Override
        public void run() {
            C.getGuild().getManager().setName("Happyheart's Winter Wonderland").queue();
            try {
                Icon icon = Icon.from(new File("winter.png"));
                C.getGuild().getManager().setIcon(icon).queue();
                for (Roles crole : Roles.values()) {
                    crole.getRole().getManager().setName(crole.getXmas()).queue();
                }
                C.getGuild().getManager().setName("Happyheart's Winter Wonderland").complete();
                C.getGuildCtrl().setNickname(C.getGuild().getMemberById("354736186516045835"), "Decorative Bot").complete();
                Main.updateTheme();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
