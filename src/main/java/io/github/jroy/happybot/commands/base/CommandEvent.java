package io.github.jroy.happybot.commands.base;

import com.jagrosh.jdautilities.command.CommandClient;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * A custom implementation of JDA-Utilities's {@link com.jagrosh.jdautilities.command.CommandEvent CommandEvent} class that makes our use-case easier.
 */
public class CommandEvent extends com.jagrosh.jdautilities.command.CommandEvent {

  public CommandEvent(MessageReceivedEvent event, String args, CommandClient client) {
    super(event, args, client);
  }

  /**
   * Wrapper for the {@link io.github.jroy.happybot.util.C C} class method to auto-fill parameters.
   */
  public boolean hasRole(Roles role) {
    return C.hasRole(getMember(), role);
  }

  /**
   * Wrapper for the {@link io.github.jroy.happybot.util.C C} class method to auto-fill parameters.
   */
  public boolean containsMention() {
    return C.containsMention(this);
  }

  /**
   * Wrapper for getting amount of mentioned users in a message.
   *
   * @return The amount of mentioned users.
   */
  public int getMentionsAmount() {
    return getMessage().getMentionedMembers().size();
  }

  /**
   * Wrapper for the {@link io.github.jroy.happybot.util.C C} class method to auto-fill parameters.
   */
  public Member getMentionedMember() {
    return C.getMentionedMember(this);
  }

  /**
   * Wrapper for the {@link io.github.jroy.happybot.util.C C} class method to auto-fill parameters.
   *
   * @param index Get's a certain mentioned member.
   */
  public Member getMentionedMember(int index) {
    return C.getMentionedMember(this, index);
  }

  /**
   * @return Gets the arguments of the command split by spaces.
   */
  public String[] getSplitArgs() {
    return getArgs().split(" ");
  }

}
