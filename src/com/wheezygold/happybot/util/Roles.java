package com.wheezygold.happybot.util;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;

/**
 * An easy way to get roles!
 */
@SuppressWarnings("unused")
public enum  Roles {

    //Staff Ranks
    HAPPYHEART("Happyoheart", "Happyoheart"),
    DEVELOPER("Developer", "Developer"),
    SUPER_ADMIN("Super Admin", "Super Admin"),
    ADMIN("Admin", "Admin"),
    MODERATOR("Moderators", "Moderator"),
    HELPER("Helpers", "Helper"),
    RECRUITER("Recruiter", "Recruiter"),

    //Punish Ranks
    EXP_SPAMMER("EXP Spammer", "EXP Spammer"),
    MUTED("Muted", "Muted"),

    //Regular Roles
    FANS("Fans", "Fans"),

    //Other Roles
    BOT("Useless Bot", "Bot");

    private String role;
    private String name;

    Roles(String role, String name) {

        this.role = role;
        this.name = name;

    }

    public String getrolename() {
        return role;
    }
    public String getname() {
        return name;
    }
    public Role getrole(Guild g) {
        for (Role lr : g.getRoles()) {
            if (lr.getName().equalsIgnoreCase(role)) {
                return lr;
            }
        }
        return null;
    }

}
