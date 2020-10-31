package io.github.jroy.happybot.commands.base;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Roles;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

/**
 * A custom implementation of JDA-Utilities's {@link com.jagrosh.jdautilities.command.Command Command} class that makes our use-case easier.
 */
@Slf4j
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
  @Nullable
  protected final Roles permissionRole;

  /**
   * The command's category
   */
  private final CommandCategory commandCategory;
  /**
   * The command's permission can be bypassed with the developer role.
   */
  private final boolean devCommand;
  /**
   * Storage for command cooldowns.
   * Null if the command has no cooldown.
   */
  @Nullable
  private HashMap<Member, OffsetDateTime> commandCooldowns;
  /**
   * Unit of time the cooldown is relative to.
   */
  @Nullable
  private ChronoUnit cooldownUnit;
  /**
   * Relative cooldown delay.
   */
  @Nullable
  private Integer cooldownDelay;
  /**
   * The command may not be used when true
   * Devs+ can bypass this
   */
  private boolean disabled = false;

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
    this(commandName, arguments, helpMessage, category, permissionRole, false);
  }

  /**
   * Constructor for commands with certain role requires for execution.
   *
   * @param commandName    Command's name to be used for execution.
   * @param arguments      Arguments of the command to be used for command help.
   * @param helpMessage    Command description to be used inside the command list.
   * @param category       Command's category to be used inside the command list.
   * @param permissionRole The role requires to execute the command.
   * @param devCommand     Can developers bypass permission check for this command?
   */
  public CommandBase(@NotNull String commandName, String arguments, String helpMessage, CommandCategory category, @Nullable Roles permissionRole, boolean devCommand) {
    this.name = commandName;
    this.arguments = arguments;
    this.help = helpMessage;
    this.category = new Category(category.toString());
    this.commandCategory = category;
    this.permissionRole = permissionRole;
    this.devCommand = devCommand;
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
    log.info("Cooldown Registered for: ^" + name + "!");
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

  /**
   * Gets the disabled state of the command.
   *
   * @return Disabled state.
   */
  public boolean isDisabled() {
    return disabled;
  }

  /**
   * Sets the disabled state of a command.
   *
   * @param disabled Disabled state.
   */
  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }

  @Override
  protected final void execute(CommandEvent event) {
    Member member = event.getMember();

    // Check if we're in a dm and if the user is still in the primary guild.
    if (member == null) {
      member = C.getGuild().getMember(event.getAuthor());
      if (member == null) {
        event.reply("You must be in " + C.getGuild().getName() + " to use commands in this bot!");
        return;
      }
    }

    // Check if we've been told to ignore commands from this user.
    if (C.hasRole(member, Roles.BOT_BANNED)) {
      return;
    }

    if (disabled && !C.hasRole(member, Roles.DEVELOPER)) {
      event.getMessage().addReaction("❌").queue();
      return;
    }

    //Handle "Complex" Permission Check
    boolean canExecute = true; //We assume the user can run the command at first and we annihilate them if they actually cannot
    if (permissionRole != null) { //permissionRole *is* null when there is no permission required
      if (!C.hasRole(member, permissionRole)) { //User does not have the permission role required for this command, disable execution
        canExecute = false;
      }

      if (devCommand && C.hasRole(member, Roles.DEVELOPER)) { //Add an exception for Developers when the devCommand flag is set to true
        canExecute = true;
      }
    }

    if (!canExecute) { //If are past permission checks determine the user doesn't have valid permission for said command, stop execution
      event.replyError(C.permMsg(permissionRole));
      return;
    }

    // developer bypasses cooldowns
    if (commandCooldowns != null && cooldownUnit != null && cooldownDelay != null && !C.hasRole(member, Roles.DEVELOPER)) {
      if (commandCooldowns.containsKey(member)) {
        long cooldown = OffsetDateTime.now().until(commandCooldowns.get(member), cooldownUnit);
        if (cooldown > 0) {
          event.replyError("You must wait "
              + cooldown + " " + cooldownUnit.toString().toLowerCase().substring(0, cooldownUnit.toString().toLowerCase().length() - 1) + "(s)"
              + " before doing that command again!");
          return;
        }
      }
      commandCooldowns.put(member, OffsetDateTime.now().plus(cooldownDelay, cooldownUnit));
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
  @Nullable
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
