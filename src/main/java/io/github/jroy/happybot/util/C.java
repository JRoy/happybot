package io.github.jroy.happybot.util;

import com.jagrosh.jdautilities.command.CommandEvent;
import io.github.jroy.happybot.Main;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.internal.utils.IOUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.channels.Channels;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The C Class provides lots of *sometimes* useful methods that make things ez-pz.
 */
@SuppressWarnings("unused")
@Slf4j
public class C {
  private static final String USER_AGENT = "happybot:io.github.jroy:v0.1 (by /u/wheezygold7931)";
  private static final Pattern MENTION_REGEX = Pattern.compile("<@!?(\\d+)>");
  private static final Map<TimeUnit, String> timeUnits = new LinkedHashMap<>();
  private static final double MATCH_THRESHOLD = 0.5;

  static {
    timeUnits.put(TimeUnit.DAYS, "d");
    timeUnits.put(TimeUnit.HOURS, "h");
    timeUnits.put(TimeUnit.MINUTES, "m");
    timeUnits.put(TimeUnit.SECONDS, "s");
    timeUnits.put(TimeUnit.MILLISECONDS, "ms");
    timeUnits.put(TimeUnit.MICROSECONDS, "μs");
    timeUnits.put(TimeUnit.NANOSECONDS, "ns");
  }

  public static Member matchMember(Member def, String string) {
    Guild guild = C.getGuild();
    Matcher matcher = MENTION_REGEX.matcher(string);
    if (matcher.matches()) {
      return guild.getMemberById(matcher.group(1));
    }

    Member closest = null;
    // minimum similarity = 0.5 - higher requires more similarity
    double distance = MATCH_THRESHOLD;

    for (Member member : guild.getMembers()) {
      double nicknameDistance = JaroWinklerDistance.apply(string, member.getEffectiveName());
      if (nicknameDistance > distance) {
        closest = member;
        distance = nicknameDistance;
      }
      double usernameDistance = JaroWinklerDistance.apply(string, member.getUser().getName());
      if (usernameDistance > distance) {
        closest = member;
        distance = usernameDistance;
      }
      double fullNameDistance = JaroWinklerDistance.apply(string, C.getFullName(member.getUser()));
      if (fullNameDistance > distance) {
        closest = member;
        distance = fullNameDistance;
      }
    }
    if (closest == null) {
      return def;
    } else {
      return closest;
    }
  }

  public static Role matchRole(String string) {
    Role closest = null;
    try {
      closest = C.getGuild().getRoleById(string);
    } catch (NumberFormatException ignored) {
    }
    if (closest != null) {
      return closest;
    }

    // minimum similarity = 0.5 - higher requires more similarity
    double distance = MATCH_THRESHOLD;

    for (Role role : C.getGuild().getRoles()) {
      if (role.isPublicRole()) {
        continue;
      }

      double nameDistance = JaroWinklerDistance.apply(string, role.getName());
      if (nameDistance > distance) {
        closest = role;
        distance = nameDistance;
      }
    }

    return closest;
  }

  public static String getPositionName(int position) {
    if (position == 1) {
      return "\uD83E\uDD47";
    } else if (position == 2) {
      return "\uD83E\uDD48";
    } else if (position == 3) {
      return "\uD83E\uDD49";
    } else {
      return C.bold("#" + position);
    }
  }

  /**
   * Formats a given numeric time in the given time int to a string with a certain precision
   */
  public static String format(long time, TimeUnit input, TimeUnit precision) {
    AtomicLong nanos = new AtomicLong(input.toNanos(time));
    List<String> times = new ArrayList<>();
    for (Map.Entry<TimeUnit, String> entry : timeUnits.entrySet()) {
      TimeUnit unit = entry.getKey();
      if (nanos.get() >= unit.toNanos(1) && precision.compareTo(unit) <= 0) {
        times.add(getTime(nanos, unit, entry.getValue()));
      }
    }
    return String.join(" ", times);
  }

  /**
   * Formats assuming the given time is in milliseocnds
   *
   * @see C#format(long, TimeUnit, TimeUnit)
   */
  public static String format(long time, TimeUnit precision) {
    return C.format(time, TimeUnit.MILLISECONDS, precision);
  }


  private static String getTime(AtomicLong time, TimeUnit unit, String suffix) {
    long nanos = unit.toNanos(1);
    if (time.get() >= nanos) {
      long amount = time.get() / nanos;
      time.set(time.get() % nanos);
      return amount + suffix;
    }
    return "";
  }

  public static java.awt.Color randomColour() {
    return new java.awt.Color(ThreadLocalRandom.current().nextInt());
  }

  /**
   * Sees if a user has the role displayed.
   *
   * @param m The member that has the role.
   * @param r The role you are testing for.
   * @return Boolean, is the person has the role.
   */
  public static boolean hasRole(Member m, Roles r) {
    if (m == null) {
      return false;
    }
    List<String> ids = getRoleIds(m.getRoles());
    return ids.contains(r.getId()) || (r.equals(Roles.SUPER_ADMIN) && ids.contains(Roles.CHANNEL_MANAGER.getId()));
  }

  /**
   * Sees if a user has the roles displayed.
   *
   * @param m The member to check.
   * @param r The roles to check
   * @return If user has all roles.
   */
  public static boolean hasRoles(Member m, Roles... r) {
    if (m == null) {
      return false;
    }
    if (r.length != 0) {
      List<String> ids = getRoleIds(m.getRoles());
      for (Roles role : r) {
        if (!ids.contains(role.getId())) {
          return false;
        }
      }
    }
    return true;
  }

  public static List<String> getRoleIds(List<Role> roles) {
    List<String> list = new ArrayList<>();
    for (Role role : roles) {
      list.add(role.getId());
    }
    return list;
  }

  /**
   * Gets the member/sender from the {@link com.jagrosh.jdautilities.command.CommandEvent CommandEvent} in the JDA Member Format.
   *
   * @param e The Command Event that you need the member from.
   * @return Returns a member from the event.
   */
  @Nonnull
  public static Member getMentionedMember(CommandEvent e) {
    return getMentionedMember(e, 0);
  }

  /**
   * Gets the member/sender from the {@link com.jagrosh.jdautilities.command.CommandEvent CommandEvent} in the JDA Member Format.
   *
   * @param e     The Command Event that you need the member from.
   * @param index The mentioned member to get
   * @return Returns a member from the event.
   */
  @SuppressWarnings("ConstantConditions")
  @Nonnull
  public static Member getMentionedMember(CommandEvent e, int index) {
    try {
      return e.getMessage().getMentionedMembers().get(index);
    } catch (IndexOutOfBoundsException exception) {
      return null;
    }
  }

  /**
   * Detects if a message contains a user mention.
   *
   * @param e The {@link com.jagrosh.jdautilities.command.CommandEvent CommandEvent} where the mention is from.
   * @return Returns if the message contains a mention.
   */
  public static boolean containsMention(CommandEvent e) {
    return e.getMessage().getMentionedUsers().size() >= 1;
  }

  /**
   * Returns the perm message for a Role.
   *
   * @param r The role the message is made for.
   * @return Returns the string of the permission message.
   */
  public static String permMsg(Roles r) {
    return "This requires the role " + C.bold(r.toString()) + " to execute!";
  }

  /**
   * Expands a short url and gives the full URL output.
   *
   * @param baseUrl The short url given for conversion.
   * @return The longer URL.
   */
  public static String urlExpand(String baseUrl) {
    URL shortURL = null;
    try {
      shortURL = new URL(baseUrl);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }

    HttpURLConnection connection = null;
    //Open the connection.
    try {
      assert shortURL != null;
      connection = (HttpURLConnection) shortURL.openConnection(Proxy.NO_PROXY);
      //We do not want to render the contents of the long url.
      connection.setInstanceFollowRedirects(false);
    } catch (IOException e) {
      e.printStackTrace();
    }
    //Grab the longer url.
    assert connection != null;
    String fullURL = connection.getHeaderField("Location");
    connection.disconnect();
    return fullURL;
  }

  /**
   * Downloads a file to chosen output location.
   *
   * @param url        The URL where the target is located.
   * @param outputName The File/Path where the file will be placed.
   */
  public static void dlFile(String url, String outputName) {
    try {
      @Cleanup FileOutputStream fos = new FileOutputStream(outputName);
      fos.getChannel().transferFrom(Channels.newChannel(new URL(url).openStream()), 0, Long.MAX_VALUE);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Gets the happyheart guild easily.
   *
   * @return Guild of happyheart fanbase.
   */
  public static Guild getGuild() {
    return Main.getJda().getGuildById(Constants.GUILD_ID.get());
  }

  /**
   * Toggles the role of a user.
   *
   * @param m    The member who you want the role on.
   * @param role The role you want toggled.
   * @return Returns if it added or removed a role.
   */
  public static boolean toggleRole(Member m, Roles role) {
    if (!hasRole(m, role)) {
      getGuild().addRoleToMember(m, role.getRole()).reason("Role toggle from internal C Util").queue();
      return true;
    } else {
      getGuild().removeRoleFromMember(m, role.getRole()).reason("Role toggle from internal C Util").queue();
      return false;
    }
  }

  /**
   * Removes a role from a guild member.
   *
   * @param m    The target guild member.
   * @param role The target role.
   */
  public static void removeRole(Member m, Roles role) {
    getGuild().removeRoleFromMember(m, role.getRole()).reason("Role removed from internal C Util").queue();
  }

  /**
   * Removes a role from a guild member.
   *
   * @param m      The target guild member.
   * @param role   The target role.
   * @param reason The reason to show in the audit log.
   */
  public static void removeRole(Member m, Roles role, String reason) {
    getGuild().removeRoleFromMember(m, role.getRole()).reason(reason).queue();
  }

  /**
   * Adds a role to a guild member.
   *
   * @param m    The target guild member.
   * @param role The target role.
   */
  public static void giveRole(Member m, Roles role) {
    getGuild().addRoleToMember(m, role.getRole()).queue();
  }

  /**
   * Adds a role to a guild member.
   *
   * @param m      The target guild member.
   * @param role   The target role.
   * @param reason The reason to show in the audit log.
   */
  public static void giveRole(Member m, Roles role, String reason) {
    getGuild().addRoleToMember(m, role.getRole()).reason(reason).queue();
  }

  /**
   * Adds multiple roles to a guild member.
   *
   * @param m     The target guild member.
   * @param roles The target roles to add to the user.
   */
  public static void giveRoles(Member m, Roles... roles) {
    List<Role> memberRoles = new ArrayList<>(m.getRoles());
    memberRoles.addAll(Arrays.asList(toRoleArray(roles)));
    getGuild().modifyMemberRoles(m, memberRoles).queue();
  }

  /**
   * Writes the first line of a file.
   *
   * @param file    The target file.
   * @param content The content of said file.
   */
  public static void writeFile(String file, String content) {
    try {
      @Cleanup FileWriter fw = new FileWriter(file);
      @Cleanup BufferedWriter bw = new BufferedWriter(fw);
      bw.write(content);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Returns a number as a String in comma formatted style.
   *
   * @param in The input number.
   * @return Comma formatted number.
   */
  public static String prettyNum(long in) {
    return NumberFormat.getInstance(Locale.US).format(in);
  }

  /**
   * Opens a private channel with a user.
   *
   * @param m       The Guild Member that gets the private channel.
   * @param message The message to send via private channel.
   */
  public static void privChannel(Member m, String message) {
    try {
      if (!m.getUser().isBot()) {
        m.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(message).queue(), throwable -> log.error("Tried to open a private channel but got error: " + throwable.getMessage()));
      }
    } catch (UnsupportedOperationException e) {
      log.error("Tried to open a private channel but got error: " + e.getMessage());
    }
  }

  @SuppressWarnings({"unused", "Duplicates"})
  public static Message privChannel(Member m, String message, boolean returnItem) {
    try {
      if (!m.getUser().isBot()) {
        final Message[] message1 = new Message[1];
        m.getUser().openPrivateChannel().queue(privateChannel -> message1[0] = privateChannel.sendMessage(message).complete(), throwable -> log.error("Tried to open a private channel but got error: " + throwable.getMessage()));
        return message1[0];
      }
    } catch (UnsupportedOperationException e) {
      log.error("Tried to open a private channel but got error: " + e.getMessage());
    }
    return null;
  }

  @SuppressWarnings({"unused", "Duplicates"})
  public static Message privChannel(Member m, MessageEmbed message, boolean returnItem) {
    try {
      if (!m.getUser().isBot()) {
        final Message[] message1 = new Message[1];
        m.getUser().openPrivateChannel().queue(privateChannel -> message1[0] = privateChannel.sendMessageEmbeds(message).complete(), throwable -> log.error("Tried to open a private channel but got error: " + throwable.getMessage()));
        return message1[0];
      }
    } catch (UnsupportedOperationException e) {
      log.error("Tried to open a private channel but got error: " + e.getMessage());
    }
    return null;
  }

  public static void privChannel(Member m, MessageEmbed embed) {
    try {
      if (!m.getUser().isBot()) {
        m.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessageEmbeds(embed).queue(), throwable -> log.error("Tried to open a private channel but got error: " + throwable.getMessage()));
      }
    } catch (UnsupportedOperationException e) {
      log.error("Tried to open a private channel but got error: " + e.getMessage());
    }
  }


  /**
   * Returns a codeblock of used value.
   *
   * @param value Text in the code block.
   * @return The codeblock.
   */
  public static String codeblock(String value) {
    return "```" + escape(value) + "```";
  }

  /**
   * Returns a small codeblock of used value.
   *
   * @param value Text in the small code block.
   * @return The small codeblock.
   */
  public static String code(String value) {
    return "`" + escape(value) + "`";
  }

  /**
   * Returns a bold string.
   *
   * @param value Text to be bold.
   * @return The bold text.
   */
  public static String bold(String value) {
    return "**" + escape(value) + "**";
  }

  /**
   * Returns an italicized string.
   *
   * @param value Text to be italicized.
   * @return The italicized text.
   */
  public static String slant(String value) {
    return "*" + escape(value) + "*";
  }

  /**
   * Returns an underlined string.
   *
   * @param value Text to be underlined.
   * @return The underlined text.
   */
  public static String underline(String value) {
    return "__" + escape(value) + "__";
  }

  /**
   * Escape a string so no formatting is applied on discord
   *
   * @param value Text to be escaped.
   * @return The escaped text.
   */
  public static String escape(String value) {
    return value
        .replace("*", "\\*")
        .replace("_", "\\_")
        .replace("`", "\\`");
  }

  /**
   * Test to see if a string can be parsed as a boolean.
   *
   * @param check String to be tested.
   * @return If string can be parsed
   */
  public static boolean containsBool(String check) {
    return check.equalsIgnoreCase("true") || check.equalsIgnoreCase("false");
  }

  /**
   * Tests for an image in target message.
   *
   * @param message The target message to be tested.
   * @return If the message contains an image.
   */
  public static boolean containsImage(Message message) {
    if (message.getAttachments().stream().anyMatch(Message.Attachment::isImage)) {
      return true;
    }
    return message.getEmbeds().stream().anyMatch(e -> e.getImage() != null || e.getVideoInfo() != null);
  }

  /**
   * Gets the image url from a message.
   *
   * @param message The target message.
   * @return The image url.
   */
  public static String getImage(Message message) {
    return message.getAttachments().get(0).getUrl();
  }

  /**
   * Gets the body of the given URL as a string
   *
   * @param urlString the URL to get the body of
   * @return the body of the URL
   */
  @SuppressWarnings("deprecation")
  public static String readUrl(String urlString) {
    try {
      HttpClient client = new DefaultHttpClient();
      HttpGet request = new HttpGet(urlString);
      request.addHeader("User-Agent", USER_AGENT);

      HttpResponse response = client.execute(request);
      InputStream content = response.getEntity().getContent();
      byte[] bytes = IOUtil.readFully(content);
      return new String(bytes);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Gets the full name of the user. Specifically, their username and their discriminator
   *
   * @param user the user to get the name of
   * @return the name and discriminator combined with a hash.
   */
  public static String getFullName(User user) {
    return user.getName() + "#" + user.getDiscriminator();
  }

  /**
   * Gets a Guild Member from their name.
   *
   * @param name The name of the memeber.
   * @return the member that matches the name otherwise, null.
   */
  @CheckForNull
  public static Member getMemberFromName(String name) {
    for (Member curMember : getGuild().getMembers()) {
      if (curMember.getUser().getName().equalsIgnoreCase(name)) {
        return curMember;
      }
    }
    for (Member curMember : getGuild().getMembers()) {
      if (curMember.getUser().getName().toLowerCase().startsWith(name.toLowerCase())) {
        return curMember;
      }
    }
    return null;
  }

  /**
   * Turns a string array into a list seperated by commas.
   *
   * @param list The string array
   * @return List seperated by commas.
   */
  public static String prettyStringArray(Collection<String> list) {
    StringBuilder builder = new StringBuilder();
    for (String curStr : list) {
      builder.append(curStr).append(", ");
    }
    builder.setLength(builder.length() - 2);
    return builder.toString();
  }

  /**
   * Turns a role array into a list of role names seperated by commas.
   *
   * @param list The role array
   * @return List seperated by commas.
   */
  public static String prettyRoleArray(Collection<Role> list) {
    StringBuilder builder = new StringBuilder();
    for (Role curRole : list) {
      builder.append(curRole.getName()).append(", ");
    }
    builder.setLength(builder.length() - 2);
    return builder.toString();
  }

  @SuppressWarnings("ToArrayCallWithZeroLengthArrayArgument")
  public static Role[] toRoleArray(Roles[] roles) {
    List<Role> list = new ArrayList<>();
    for (Roles curRole : roles) {
      list.add(curRole.getRole());
    }
    return list.toArray(new Role[list.size()]);
  }
}
