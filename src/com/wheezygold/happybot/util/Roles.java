package com.wheezygold.happybot.util;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;

/**
 * An easy way to get roles!
 */
@SuppressWarnings("unused")
public enum  Roles {

    //Staff Ranks
    HAPPYHEART("264560287183667202", "Happoheart", "Spookoheart", "Santoheart", "Happoheart"),
    DEVELOPER("317386352763207702", "Developer", "Grim Reaper", "Toy Maker", "Developer"),
    SUPER_ADMIN("264963855426256897", "Super Admin", "Sp00ky Skary Skelentons", "Gingerbread Things", "Super Admin"),
    CHANNEL_MANAGER("368172637182230549", "Channel Manager", "Rabid Werewolves", "Management Elfs", "Channel Manager"),
    ADMIN("295673671203291147", "Admin", "Glowing Pumpkins", "Tree Ornaments", "Admin"),
    MODERATOR("264964563517046784", "Moderators", "Fortune Tellers", "Frosty Snowmen", "Moderator"),
    HELPER("264965005949009920", "Helpers", "Drooling Vampires", "Stocking Stuffers", "Helper"),
    RECRUITER("299632971319869451", "Recruiter", "Recruiter", "Recruiter", "Recruiter"),

    //Punish Ranks
    EXP_SPAMMER("299652763099332608", "EXP Spammer", "EXP Spammer", "EXP Spammer", "EXP Spammer"),
    MUTED("280043630494875648", "Muted", "Muted", "Muted", "Muted"),

    //Regular Roles
    LEGENDARY("348917170174033920", "Legendary", "Ghost Pirates", "Wrapped Presents", "Legendary"),
    PATRON_BOYS("300081997878132736", "Patron Boys", "Trick or Treaters", "Reindeer's Carrots", "Patron Boys"),
    SUPPORTER("294583863521312770", "Supporter", "Enslaved", "Half-Eaten Cookie", "Supporter"),
    QUALITY_ART("364568257660846084", "Quality Art", "Quality Spooks", "Quality Snowflakes", "Quality Art"),
    OG("350986983004307456", "OG", "Decaying Corpses", "Original Elf", "OG"),
    IRL("304074646104571905", "IRL", "Zombies", "Nut Crackers", "IRL"),
    OBSESSIVE("296259032400920578", "Obsessive", "Crazed Flesh Eater", "Scrooges", "Obsessive"),
    TRYHARD("296257773887553538", "Tryhard", "Human Filth", "Coal Sack", "Tryhard"),
    REGULAR("277933832240496650", "Regular", "Regular Costume", "Pinecone", "Regular"),
    FANS("264964418796781568", "Fans", "Cult Fanatics", "Naughty List", "Fans"),

    //Mentionable Roles
    TWITTER("366005538766585866", "Twitter", "Twitter", "Twitter", "Twitter"),
    GIT("366056865697628160", "Git", "Git", "Git", "Git"),
    UPDATES("366056967287865345", "Updates", "Updates", "Updates", "Updates"),

    //Other Roles
    BOT("285371566580170753", "Useless Bot", "Useless Bot", "Useless Bot", "Bot"),
    EVERYONE("237363812842340363", "@everyone", "@everyone", "@everyone", "@everyone");

    private String nrole;
    private String srole;
    private String crole;
    private String name;
    private String id;

    Roles(String id, String nrole, String srole, String crole, String name) {

        this.nrole = nrole;
        this.srole = srole;
        this.crole = crole;
        this.name = name;
        this.id = id;

    }

    public String getrolename() {
        return nrole;
    }
    public String getname() {
        return name;
    }
    public String getspook() { return srole; }
    public String getxmas() { return crole; }
    public Role getrole(Guild g) {
        return C.getGuild().getRoleById(id);
    }

}
