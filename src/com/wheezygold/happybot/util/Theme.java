package com.wheezygold.happybot.util;

import com.wheezygold.happybot.Main;
import net.dv8tion.jda.core.entities.Icon;

import java.io.File;
import java.io.IOException;

public class Theme {

    public static void toNormal() {
        NormalTheme normalTheme = new NormalTheme();
        Thread t = new Thread(normalTheme);
        t.start();
    }

    public static void toSpooky() {
        SpookyTheme spookyTheme = new SpookyTheme();
        Thread t = new Thread(spookyTheme);
        t.start();
    }

    private static class SpookyTheme implements Runnable {

        public SpookyTheme() {}

        @Override
        public void run() {
            C.getGuild().getManager().setName("Happyheart Spookbase").queue();
            try {
                Icon icon = Icon.from(new File("spooky.png"));
                C.getGuild().getManager().setIcon(icon).queue();
                for (Roles crole : Roles.values()) {
                    crole.getrole(C.getGuild()).getManager().setName(crole.getspook()).queue();
                }
                C.getGuild().getManager().setName("Happyheart Spookbase").complete();
                C.getGuildCtrl().setNickname(C.getGuild().getMemberById("354736186516045835"), "Spooky Bot").complete();
                Main.updateTheme();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class NormalTheme implements Runnable {

        public NormalTheme() {}

        @Override
        public void run() {
            C.getGuild().getManager().setName("Happyheart Fanbase").queue();
            try {
                Icon icon = Icon.from(new File("normal.png"));
                C.getGuild().getManager().setIcon(icon).queue();
                for (Roles crole : Roles.values()) {
                    crole.getrole(C.getGuild()).getManager().setName(crole.getrolename()).queue();
                }
                C.getGuild().getManager().setName("Happyheart Fanbase").complete();
                C.getGuildCtrl().setNickname(C.getGuild().getMemberById("354736186516045835"), "Happy Bot").complete();
                Main.updateTheme();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
