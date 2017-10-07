package com.wheezygold.happybot.util;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.Main;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.managers.GuildController;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.channels.Channels;
import java.util.Objects;

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
            //Don't Worry this will not hapen
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

    /**
     * Returns the perm message for a Role.
     * @param r The role the message is made for.
     * @return Returns the string of the permission message.
     */
    public static String permMsg(Roles r) {
        return "This requires Permission Rank **" + r.getname() + "** to execute!";
    }

    /**
     * Gets the {@link net.dv8tion.jda.core.managers.GuildController GuildController} Class from a {@link com.jagrosh.jdautilities.commandclient.CommandEvent CommandEvent}.
     * @param e The target {@link com.jagrosh.jdautilities.commandclient.CommandEvent CommandEvent}
     * @return The requested {@link net.dv8tion.jda.core.managers.GuildController GuildController}
     */
    public static GuildController getCtrl(CommandEvent e) {
        return e.getGuild().getController();
    }

    /**
     * Expands a short url and gives the full URL output.
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
     * @param url The URL where the target is located.
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
     * @return {@link net.dv8tion.jda.core.entities.Guild Guild} of happyheart fanbase.
     */
    public static Guild getGuild() {
        return Main.getJda().getGuildById("237363812842340363");
    }

    /**
     * Gets the happyheart guild controller.
     * @return {@link net.dv8tion.jda.core.managers.GuildController GuildController} of happyheart guild.
     */
    public static GuildController getGuildCtrl() { return Main.getJda().getGuildById("237363812842340363").getController(); }

    /**
     * Sends the help message for the registered commands.
     * @param event The {@link com.jagrosh.jdautilities.commandclient.CommandEvent CommandEvent} that handles the reply.
     * @return The help message.
     */
    public static String showHelp(CommandEvent event) {
        event.replySuccess("Help is on the way! :sparkles:");
        StringBuilder builder = new StringBuilder("**"+event.getSelfUser().getName()+"** commands:\n");
        Command.Category category = null;
        for(Command command : event.getClient().getCommands())
            if(!command.isOwnerCommand() || event.isOwner() || event.isCoOwner())
            {
                if(!Objects.equals(category, command.getCategory()))
                {
                    category = command.getCategory();
                    builder.append("\n\n  __").append(category==null ? "No Category" : category.getName()).append("__:\n");
                }
                builder.append("\n`").append(event.getClient().getPrefix()).append(command.getName())
                        .append(command.getArguments()==null ? "`" : " "+command.getArguments()+"`")
                        .append(" **-** ").append(command.getHelp());
            }
        User owner = event.getJDA().getUserById(event.getClient().getOwnerId());
        if(owner!=null)
        {
            builder.append("\n\nFor additional help, contact **").append(owner.getName()).append("**#").append(owner.getDiscriminator());

    }
        return builder.toString();
    }

    /**
     * Toggles the role of a user.
     * @param m The member who you want the role on.
     * @param role The role you want toggled.
     * @return Returns if it added or removed a role.
     */
    public static boolean toggleRole(Member m, Roles role) {
        if (!hasRole(getGuild(), m, role)) {
            getGuildCtrl().addSingleRoleToMember(m, role.getrole(getGuild())).reason("Role toggle from internal C Util").queue();
            return true;
        } else {
            getGuildCtrl().removeSingleRoleFromMember(m, role.getrole(getGuild())).reason("Role toggle from internal C Util").queue();
            return false;
        }
    }

}