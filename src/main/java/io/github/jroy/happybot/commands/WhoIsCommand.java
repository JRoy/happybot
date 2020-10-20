package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.C;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class WhoIsCommand extends CommandBase {
    // Thanks Okx.
    private static final String TECHNOCODER_ID = "180899128006410240";

  public WhoIsCommand() {
    super("whois", "[<user>]", "Gives you information about you or another user.", CommandCategory.GENERAL);
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    Member targetMember = e.getMember();

    if (!e.getArgs().isEmpty()) {
      targetMember = C.matchMember(targetMember, e.getArgs());
    }
    User targetUser = targetMember.getUser();

    EmbedBuilder embed = new EmbedBuilder();
    embed.setAuthor(C.getFullName(targetUser), null, targetUser.getAvatarUrl()).setThumbnail(targetUser.getAvatarUrl());
    embed.setDescription(targetMember.getAsMention()).setFooter("ID: " + targetUser.getId() + " • " + "Avatar ID: " + targetUser.getAvatarId(), null);
    embed.addField("Status", targetMember.getOnlineStatus().getKey(), true);

    ZonedDateTime joinDate = targetMember.getTimeJoined().atZoneSameInstant(ZoneId.of("America/New_York"));
    if (targetUser.getId().equals(TECHNOCODER_ID))
        joinDate = ZonedDateTime.of(2017, 3, 24, 5, 36, 0, 0, ZoneId.of("America/New_York"));

    embed.addField("Joined", joinDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US) + ", " + joinDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.US) + " " + joinDate.getDayOfMonth() + ", " + joinDate.getYear() + " " + joinDate.getHour() % 12 + ":" + joinDate.getMinute() + " " + ((joinDate.getHour() >= 12) ? "PM" : "AM"), true);

    List<Member> joinPosSort = new ArrayList<>(e.getGuild().getMembers());
    joinPosSort.sort(Comparator.comparing(Member::getTimeJoined));
    int joinPosition = joinPosSort.indexOf(targetMember);
    if (targetUser.getId().equals(TECHNOCODER_ID)) joinPosition = 39;
    embed.addField("Join Position", joinPosition + "/" + joinPosSort.size(), true);

    ZonedDateTime registerDate = targetUser.getTimeCreated().atZoneSameInstant(ZoneId.of("America/New_York"));
    embed.addField("Registered", joinDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US) + ", " + registerDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.US) + " " + registerDate.getDayOfMonth() + ", " + registerDate.getYear() + " " + registerDate.getHour() % 12 + ":" + registerDate.getMinute() + " " + ((registerDate.getHour() >= 12) ? "PM" : "AM"), true);

    StringBuilder roles = new StringBuilder();
    int roleCount = 0;
    for (Role curRole : targetMember.getRoles()) {
      roleCount++;
      roles.append(curRole.getAsMention()).append(" ");
    }
    embed.addField("Roles [" + roleCount + "]", roles.toString(), false);
    StringBuilder perms = new StringBuilder();
    for (Permission curPerm : Permission.values()) {
      if (targetMember.hasPermission(curPerm)) {
        perms.append(curPerm.getName()).append(", ");
      }
    }
    perms.setLength(perms.length() - 2);
    embed.addField("Permissions", perms.toString(), false);

    e.reply(embed.build());
  }
}
