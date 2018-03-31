package io.github.jroy.happybot.util;

import net.dv8tion.jda.core.entities.Role;

/**
 * An easy way to get roles!
 */
@SuppressWarnings("unused")
public enum Roles {

    //Staff Ranks
    HAPPYHEART("264560287183667202", "Happoheart", "Happoheart"),
    DEVELOPER("317386352763207702", "Developer", "Developer"),
    SUPER_ADMIN("264963855426256897", "Super Admin", "Super Admin"),
    CHANNEL_MANAGER("368172637182230549", "Channel Manager", "Channel Manager"),
    //ADMIN("295673671203291147", "Admin", "Admin"),
    MODERATOR("264964563517046784", "Moderators", "Moderator"),
    HELPER("264965005949009920", "Trial Moderators", "Trial Mod"),
    RECRUITER("299632971319869451", "Recruiter", "Recruiter"),

    //Punish Ranks
    EXP_SPAMMER("299652763099332608", "EXP Spammer", "EXP Spammer"),
    MUTED("280043630494875648", "Muted", "Muted"),

    //Regular Roles
    LEGENDARY("348917170174033920", "Legendary", "Legendary"),
    PATRON_BOYS("300081997878132736", "Patron Boys", "Patron Boys"),
    SUPPORTER("294583863521312770", "Supporter", "Supporter"),
    QUALITY_ART("364568257660846084", "Quality Art", "Quality Art"),
    OG("350986983004307456", "OG", "OG"),
    IRL("304074646104571905", "IRL", "IRL"),
    OBSESSIVE("296259032400920578", "Obsessive", "Obsessive"),
    ADDICT("384503288621694976", "Gambling Addict", "Gambling Addict"),
    TRYHARD("296257773887553538", "Tryhard", "Tryhard"),
    REGULAR("277933832240496650", "Regular", "Regular"),
    FANS("264964418796781568", "Fans",  "Fans"),

    //Mentionable Roles
    TWITTER("366005538766585866", "Twitter", "Twitter"),
    GIT("366056865697628160", "Git","Git"),
    UPDATES("366056967287865345", "Updates", "Updates"),

    //Gamble ROles
    GAMBLE1("389118818753970177", "gamble1.5", "gamble1.5"),
    GAMBLE2("389118899989250050", "gamble2", "gamble2"),

    //Other Roles
    ETHAN("369616007884701708", "Ethan", "Ethan"),
    BOT("285371566580170753", "Useless Bot", "Bot"),
    EVERYONE("237363812842340363", "@everyone", "@everyone");

    private String nrole;
    private String name;
    private String id;

    Roles(String id, String nrole, String name) {

        this.nrole = nrole;
        this.name = name;
        this.id = id;

    }

    public String getId() { return id; }

    public String getRoleName() {
        return nrole;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return C.getGuild().getRoleById(id);
    }

}
