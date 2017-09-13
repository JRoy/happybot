package com.wheezygold.happybot.Util;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

public class C {

    public static boolean hasRole(Guild g, Member m, Roles r) {
        try {
            for (Role s : g.getRolesByName(r.getrolename(), true)) {
                return m.getRoles().contains(s);
            }
        } catch (NullPointerException x) {
            System.out.println("Could not find a role with that name!");
        }
        return false;
    }

    public static void log(String s) {
        System.out.println("[HappyBot] " + s);
    }

    public static Member getMemberEvent(CommandEvent e) {
        for (User m : e.getMessage().getMentionedUsers()) {
            return e.getGuild().getMember(m);
        }
        return null;
    }

//    public static Role getRole(Guild g, Roles r) {
//        for (Role lr : g.getRoles()) {
//            if (lr.getName().equalsIgnoreCase(r.getrolename())) {
//
//            }
//        }
//    }

    public static String permMsg(Roles r) {
        return "This requires Permission Rank **" + r.getname() + "** to execute!";
    }

}
