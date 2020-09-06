package io.github.jroy.happybot.events;

import io.github.jroy.happybot.util.Channels;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;

public class SubAlerts extends ListenerAdapter {

  @Override
  public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent event) {
    if (OffsetDateTime.now().toEpochSecond() - event.getMember().getTimeJoined().toEpochSecond() < 10) {
      // Don't log roles from joins.
      return;
    }

    for (Role role : event.getRoles()) {
      if (role.getId().equals(Roles.TWITCH_SUB.getId())) {
        Channels.SUB_ALERTS.sendMessage(event.getMember().getAsMention() + " has subscribed to happyheart on Twitch!");
      } else if (role.getId().equals(Roles.PATRON_BOYS.getId())) {
        Channels.SUB_ALERTS.sendMessage(event.getMember().getAsMention() + " has pledged to happyheart on Patreon!");
      }
    }
  }
}
