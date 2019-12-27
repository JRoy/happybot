package io.github.jroy.happybot.util;

public enum Emote {

  SPINNER("354100265852600322"),
  SADHEART("359429261762887680"),
  RESOURCE_PACK("354789422140489729"),
  HAPPYWEEB("354789048683855872"),
  HAPPYTRASH("357913402745552897"),
  HAPPYLAUGH("367354969495699456"),
  HAPPYL("368148164097736721"),
  HAPPYIRL("318023107333849098"),
  HAPPYGASM("354405601641496577"),
  HAPPYBOI("368147929753452544"),
  HAPPY_THONK("350737034928979968"),
  HACKCLIENT("355071947920375808"),
  GITGUD("354974389986394122"),
  BOBROSSHEART("366278593187282954"),
  HAPPPYGIR("376180982698737675"),
  SPOOKYHEART("369237560372756480");

  private String emote;

  Emote(String emote) {
    this.emote = emote;
  }

  public static Emote getRandom() {
    return values()[(int) (Math.random() * values().length)];
  }

  public String getId() {
    return emote;
  }

  public net.dv8tion.jda.api.entities.Emote getEmote() {
    return C.getGuild().getEmoteById(emote);
  }

}
