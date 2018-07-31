package io.github.jroy.happybot.commands.base;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Logger;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.core.entities.Member;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

/**
 * A custom implementation of JDA-Utilities's {@link com.jagrosh.jdautilities.command.Command Command} class that makes our use-case easier.
 */
@SuppressWarnings("WeakerAccess")
public abstract class CommandBase extends Command {
  /**
   * Command usage
   */
  protected final String invalid;

  /**
   * The role that is required to run the target command.
   * <p>
   * Will be null if no role is required
   */
  protected final Roles permissionRole;

  /**
   * The command's category
   */
  private final CommandCategory commandCategory;

  /**
   * Storage for command cooldowns.
   * Null if the command has no cooldown.
   */
  private HashMap<Member, OffsetDateTime> commandCooldowns;

  /**
   * Unit of time the cooldown is relative to.
   */
  private ChronoUnit cooldownUnit;

  /**
   * Relative cooldown delay.
   */
  private Integer cooldownDelay;

  /**
   * Constructor for commands with no role permissions requires for execution.
   *
   * @param commandName Command's name to be used for execution.
   * @param arguments   Arguments of the command to be used for command help.
   * @param helpMessage Command description to be used inside the command list.
   * @param category    Command's category to be used inside the command list.
   */
  public CommandBase(@NotNull String commandName, String arguments, String helpMessage, CommandCategory category) {
    this(commandName, arguments, helpMessage, category, null);
  }

  /**
   * Constructor for commands with certain role requires for execution.
   *
   * @param commandName    Command's name to be used for execution.
   * @param arguments      Arguments of the command to be used for command help.
   * @param helpMessage    Command description to be used inside the command list.
   * @param category       Command's category to be used inside the command list.
   * @param permissionRole The role requires to execute the command.
   */
  public CommandBase(@NotNull String commandName, String arguments, String helpMessage, CommandCategory category, Roles permissionRole) {
    this.name = commandName;
    this.arguments = arguments;
    this.help = helpMessage;
    this.category = new Category(category.toString());
    this.commandCategory = category;
    this.permissionRole = permissionRole;
    this.commandCooldowns = null;
    this.cooldownUnit = null;
    this.cooldownDelay = null;
    this.invalid = C.bold("Correct Usage:") + " ^" + name + " " + arguments;
  }

  /**
   * Ease method for setting cooldowns in seconds.
   *
   * @param seconds Time in seconds for the cooldown.
   * @see CommandBase#setCooldown(int, ChronoUnit)
   */
  public void setCooldownSeconds(int seconds) {
    setCooldown(seconds, ChronoUnit.SECONDS);
  }

  /**
   * Set's the amount of time it takes for a user to use each command.
   *
   * @param amount     Amount of cooldown.
   * @param chronoUnit Unit of measure for the cooldown amount.
   */
  public void setCooldown(int amount, ChronoUnit chronoUnit) {
    commandCooldowns = new HashMap<>();
    cooldownUnit = chronoUnit;
    cooldownDelay = amount;
    Logger.info("Cooldown Registered for: ^" + name + "!");
  }

  public void removeFromCooldown(Member member) {
    if (commandCooldowns != null) {
      commandCooldowns.remove(member);
    }
  }

  /**
   * Removes a command's cooldown.
   */
  public void removeCooldown() {
    commandCooldowns = null;
    cooldownUnit = null;
    cooldownDelay = null;
  }

  @Override
  protected void execute(CommandEvent event) {
    Member member = event.getMember();

//        if (event.getTextChannel().getId().equals(Channels.RANDOM.getId()) && (!C.hasRole(event.getMember(), Roles.DEVELOPER) && !C.hasRole(event.getMember(), Roles.HELPER))) {
//            event.getMessage().addReaction("❌").queue();
//            return;
//        }

    if (permissionRole != null && !C.hasRole(member, permissionRole)) {
      event.replyError(C.permMsg(permissionRole));
      return;
    }

    // developer bypasses cooldowns
    if (commandCooldowns != null && !C.hasRole(member, Roles.DEVELOPER)) {
      if (commandCooldowns.containsKey(member)) {
        long cooldown = OffsetDateTime.now().until(commandCooldowns.get(member), cooldownUnit);
        if (cooldown > 0) {
          event.replyError("You must wait "
              + cooldown + " " + cooldownUnit.toString().toLowerCase().substring(0, cooldownUnit.toString().toLowerCase().length() - 1) + "(s)"
              + " before doing that command again!");
          return;
        }
      }
      commandCooldowns.put(event.getMember(), OffsetDateTime.now().plus(cooldownDelay, cooldownUnit));
    }
    executeCommand(new io.github.jroy.happybot.commands.base.CommandEvent(event.getEvent(), event.getArgs(), event.getClient()));
  }

  /**
   * Triggered when the command is ran and the user has the required permission.
   *
   * @param event The information associated with the command calling
   */
  protected abstract void executeCommand(io.github.jroy.happybot.commands.base.CommandEvent event);

  /**
   * @return Command's Permission
   */
  public Roles getPermissionRole() {
    return permissionRole;
  }

  /**
   * @return Command's Category
   */
  public CommandCategory getCommandCategory() {
    return commandCategory;
  }
}
