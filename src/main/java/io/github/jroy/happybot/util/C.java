package io.github.jroy.happybot.util;

import com.jagrosh.jdautilities.command.CommandEvent;
import io.github.jroy.happybot.Main;
import net.dean.jraw.http.UserAgent;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.managers.GuildController;
import net.dv8tion.jda.core.utils.IOUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import javax.annotation.Nonnull;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.channels.Channels;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * The C Class provides lots of *sometimes* useful methods that make things ez-pz.
 */
public class C {
    private static String USER_AGENT = new UserAgent("happybot", "io.github.jroy", "v0.1", "wheezygold7931").toString();

    /**
     * Sees if a user has the role displayed.
     *
     * @param m The member that has the role.
     * @param r The role you are testing for.
     * @return Boolean, is the person has the role.
     */
    public static boolean hasRole(Member m, Roles r) {
        try {
            for (Role s : m.getRoles()) {
                if (r.equals(Roles.SUPER_ADMIN) && s.getId().equals(Roles.CHANNEL_MANAGER.getId()))
                    return true;
                if (s.getId().equals(r.getRole().getId())) {
                    return true;
                }
            }
        } catch (NullPointerException ignored) {}
        return false;
    }

    /**
     * Logs the message you provide.
     *
     * @param s Message that you wish to log in the bot format.
     */
    @Deprecated
    public static void log(String s) {
        System.out.println("[HappyBot] " + s);
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
     * @param e The Command Event that you need the member from.
     * @param index The mentioned member to get
     * @return Returns a member from the event.
     */
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
     * Gets the {@link net.dv8tion.jda.core.managers.GuildController GuildController} Class from a {@link com.jagrosh.jdautilities.command.CommandEvent CommandEvent}.
     *
     * @param e The target {@link com.jagrosh.jdautilities.command.CommandEvent CommandEvent}
     * @return The requested {@link net.dv8tion.jda.core.managers.GuildController GuildController}
     */
    public static GuildController getCtrl(CommandEvent e) {
        return e.getGuild().getController();
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
            connection = (HttpURLConnection) shortURL.openConnection(Proxy.NO_PROXY);
            //We do not want to render the contents of the long url.
            connection.setInstanceFollowRedirects(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Grab the longer url.
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
            FileOutputStream fos = new FileOutputStream(outputName);
            fos.getChannel().transferFrom(Channels.newChannel(new URL(url).openStream()), 0, Long.MAX_VALUE);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the happyheart guild easily.
     *
     * @return {@link net.dv8tion.jda.core.entities.Guild Guild} of happyheart fanbase.
     */
    public static Guild getGuild() {
        return Main.getJda().getGuildById(Constants.GUILD_ID.get());
    }

    /**
     * Gets the happyheart guild controller.
     *
     * @return {@link net.dv8tion.jda.core.managers.GuildController GuildController} of happyheart guild.
     */
    public static GuildController getGuildCtrl() {
        return getGuild().getController();
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
            getGuildCtrl().addSingleRoleToMember(m, role.getRole()).reason("Role toggle from internal C Util").queue();
            return true;
        } else {
            getGuildCtrl().removeSingleRoleFromMember(m, role.getRole()).reason("Role toggle from internal C Util").queue();
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
        getGuildCtrl().removeSingleRoleFromMember(m, role.getRole()).reason("Role removed from internal C Util").queue();
    }

    /**
     * Removes a role from a guild member.
     *
     * @param m      The target guild member.
     * @param role   The target role.
     * @param reason The reason to show in the audit log.
     */
    public static void removeRole(Member m, Roles role, String reason) {
        getGuildCtrl().removeSingleRoleFromMember(m, role.getRole()).reason(reason).queue();
    }

    /**
     * Adds a role to a guild member.
     *
     * @param m    The target guild member.
     * @param role The target role.
     */
    public static void giveRole(Member m, Roles role) {
        getGuildCtrl().addSingleRoleToMember(m, role.getRole()).reason("Role added from internal C Util").queue();
    }

    /**
     * Adds a role to a guild member.
     *
     * @param m      The target guild member.
     * @param role   The target role.
     * @param reason The reason to show in the audit log.
     */
    public static void giveRole(Member m, Roles role, String reason) {
        getGuildCtrl().addSingleRoleToMember(m, role.getRole()).reason(reason).queue();
    }

    /**
     * Writes the first line of a file.
     *
     * @param file    The target file.
     * @param content The content of said file.
     */
    public static void writeFile(String file, String content) {
        try {
            FileWriter fw = new FileWriter(new File(file));
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
            fw.close();
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
    public static String prettyNum(int in) {
        return NumberFormat.getInstance(Locale.US).format(in);
    }

    /**
     * Opens a private channel with a user.
     *
     * @param m       The Guild {@link net.dv8tion.jda.core.entities.Member Member} that gets the private channel.
     * @param message The message to send via private channel.
     */
    public static void privChannel(Member m, String message) {
        try {
            if (!m.getUser().isBot()) {
                m.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(message).queue(), throwable -> Logger.error("Tried to open a private channel but got error: " + throwable.getMessage()));
            }
        } catch (UnsupportedOperationException e) {
            Logger.error("Tried to open a private channel but got error: " + e.getMessage());
        }
    }

    @SuppressWarnings({"unused", "Duplicates"})
    public static Message privChannel(Member m, String message, boolean returnItem) {
        try {
            if (!m.getUser().isBot()) {
                final Message[] message1 = new Message[1];
                m.getUser().openPrivateChannel().queue(privateChannel -> {
                    message1[0] = privateChannel.sendMessage(message).complete();
                }, throwable -> Logger.error("Tried to open a private channel but got error: " + throwable.getMessage()));
                return message1[0];
            }
        } catch (UnsupportedOperationException e) {
            Logger.error("Tried to open a private channel but got error: " + e.getMessage());
        }
        return null;
    }

    @SuppressWarnings({"unused", "Duplicates"})
    public static Message privChannel(Member m, MessageEmbed message, boolean returnItem) {
        try {
            if (!m.getUser().isBot()) {
                final Message[] message1 = new Message[1];
                m.getUser().openPrivateChannel().queue(privateChannel -> {
                    message1[0] = privateChannel.sendMessage(message).complete();
                }, throwable -> Logger.error("Tried to open a private channel but got error: " + throwable.getMessage()));
                return message1[0];
            }
        } catch (UnsupportedOperationException e) {
            Logger.error("Tried to open a private channel but got error: " + e.getMessage());
        }
        return null;
    }

    public static void privChannel(Member m, MessageEmbed embed) {
        try {
            if (!m.getUser().isBot()) {
                m.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(embed).queue(), throwable -> Logger.error("Tried to open a private channel but got error: " + throwable.getMessage()));
            }
        } catch (UnsupportedOperationException e) {
            Logger.error("Tried to open a private channel but got error: " + e.getMessage());
        }
    }


    /**
     * Returns a codeblock of used value.
     * @param value Text in the code block.
     * @return The codeblock.
     */
    public static String codeblock(String value) {
        return "```" + value + "```";
    }

    /**
     * Returns a small codeblock of used value.
     * @param value Text in the small code block.
     * @return The small codeblock.
     */
    public static String code(String value) {
        return "`" + value + "`";
    }

    /**
     * Returns a bold string.
     * @param value Text to be bold.
     * @return The bold text.
     */
    public static String bold(String value) {
        return "**" + escape(value) + "**";
    }

    /**
     * Returns an italicized string.
     * @param value Text to be italicized.
     * @return The italicized text.
     */
    public static String slant(String value) {
        return "*" + escape(value) + "*";
    }

    /**
     * Returns an underlined string.
     * @param value Text to be underlined.
     * @return The underlined text.
     */
    public static String underline(String value) {
        return "__" + escape(value) + "__";
    }

    /**
     * Escape a string so no formatting is applied on discord
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
     * @param check String to be tested.
     * @return If string can be parsed
     */
    public static boolean containsBool(String check) {
        return check.equalsIgnoreCase("true") || check.equalsIgnoreCase("false");
    }

    /**
     * Tests for an image in target message.
     * @param message The target message to be tested.
     * @return If the message contains an image.
     */
    public static boolean containsImage(Message message) {
        if (message.getAttachments().stream().anyMatch(Message.Attachment::isImage))
            return true;
        return message.getEmbeds().stream().anyMatch(e -> e.getImage() != null || e.getVideoInfo() != null);
    }

    /**
     * Gets the image url from a message.
     * @param message The target message.
     * @return The image url.
     */
    public static String getImage(Message message) {
        return message.getAttachments().get(0).getUrl();
    }

    /**
     * Gets the body of the given URL as a string
     * @param urlString the URL to get the body of
     * @return the body of the URL
     */
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
     * @param user the user to get the name of
     * @return the name and discriminator combined with a hash.
     */
    public static String getFullName(User user) {
        return user.getName() + "#" + user.getDiscriminator();
    }
}
