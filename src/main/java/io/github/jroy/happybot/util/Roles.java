package io.github.jroy.happybot.util;

import net.dv8tion.jda.api.entities.Role;

/**
 * An easy way to get roles!
 */
public enum Roles {

  //Staff Ranks
  HAPPYHEART("264560287183667202", "Happoheart"),
  DEVELOPER("317386352763207702", "Developer"),
  SUPER_ADMIN("264963855426256897", "Super Admin"),
  CHANNEL_MANAGER("368172637182230549", "Channel Manager"),
  //ADMIN("295673671203291147", "Admin", "Admin"),
  MODERATOR("264964563517046784", "Moderator"),
  HELPER("264965005949009920", "Trial Mod"),

  //Punish Ranks
  EXP_SPAMMER("299652763099332608", "EXP Spammer"),
  MUTED("280043630494875648", "Muted"),

  //Money Ranks
  PATRON_BOYS("300081997878132736", "Patron Boys"),
  TWITCH_SUB("752241671189692527", "Twitch Subscriber"),
  SUPPORTER("294583863521312770", "Supporter"),

  //Regular Roles
  QUALITY_ART("364568257660846084", "Quality Art"),
  LEGENDARY("348917170174033920", "Legendary"),
  OG("350986983004307456", "OG"),
  IRL("304074646104571905", "IRL"),
  OBSESSIVE("296259032400920578", "Obsessive"),
  ADDICT("384503288621694976", "Gambling Addict"),
  TRYHARD("296257773887553538", "Tryhard"),
  REGULAR("277933832240496650", "Regular"),
  FANS("264964418796781568", "Fans"),

  //Mentionable Roles
  TWITTER("366005538766585866", "Twitter"),
  GIT("366056865697628160", "Git"),
  UPDATES("366056967287865345", "Updates"),
  TWITCH("747201597477617825", "Twitch"),

  //Gamble ROles
  GAMBLE1("389118818753970177", "gamble1.5"),
  GAMBLE2("389118899989250050", "gamble2"),
  GAMBLE3("751998987376001104", "gamble2.5"),

  //Other Roles
  BOT("285371566580170753", "Useless Bot"),
  EVERYONE("237363812842340363", "@everyone");

  private final String name;
  private final String id;

  Roles(String id, String name) {
    this.name = name;
    this.id = id;
  }

  public static Roles getRole(Role role) {
    for (Roles roles : Roles.values()) {
      if (roles.getId().equals(role.getId())) {
        return roles;
      }
    }
    return null;
  }

  public String getId() {
    return id;
  }

  public String getRoleName() {
    return name;
  }

  public String toString() {
    return name;
  }

  public Role getRole() {
    return C.getGuild().getRoleById(id);
  }
}
