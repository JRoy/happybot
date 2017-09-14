package com.wheezygold.happybot.Util;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

public class C {

    /**
     * Sees if a user has the role displayed.
     * @param g The guild the Member has the role in.
     * @param m The member that has the role.
     * @param r The role you are testing for.
     * @return Boolean, is the person has the role.
     */
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

    /**
     * Logs the message you provide.
     * @param s Message that you wish to log in the bot format.
     */
    public static void log(String s) {
        System.out.println("[HappyBot] " + s);
    }

    /**
     * Gets the member/sender from the {@link com.jagrosh.jdautilities.commandclient.CommandEvent CommandEvent} in the JDA Member Format.
     * @param e The Command Event that you need the member from.
     * @return Returns a member from the event.
     */
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

    /**
     * Returns the perm message for a Role.
     * @param r The role the message is made for.
     * @return Returns the string of the permission message.
     */
    public static String permMsg(Roles r) {
        return "This requires Permission Rank **" + r.getname() + "** to execute!";
    }

}
