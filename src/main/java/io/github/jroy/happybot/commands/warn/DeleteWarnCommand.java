package io.github.jroy.happybot.commands.warn;

import io.github.jroy.happybot.Main;
import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.sql.WarningManager;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Channels;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.util.Objects;

public class DeleteWarnCommand extends CommandBase {

  private final WarningManager warningManager;

  public DeleteWarnCommand(WarningManager warningManager) {
    super("delwarn", "<warning ID>", "Deletes the target warning.", CommandCategory.STAFF, Roles.HELPER);
    this.aliases = new String[]{"delwarning", "deletewarn", "deletewarning", "dwarn"};
    this.warningManager = warningManager;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (!e.getArgs().isEmpty() && StringUtils.isNumeric(e.getArgs())) {
      int id = Integer.parseInt(e.getArgs());
      if (warningManager.isValidWarning(id)) {
        User tUser = Main.getJda().getUserById(warningManager.getWarnTargetId(id));
        Channels.LOG.getChannel().sendMessage(new EmbedBuilder()
            .setAuthor(C.getFullName(e.getMember().getUser()), null, e.getMember().getUser().getAvatarUrl())
            .setColor(Color.YELLOW)
            .setThumbnail(Objects.requireNonNull(tUser).getAvatarUrl())
            .setDescription(":information_source: **Warning Deleted**\n" + "⚠ " + C.bold("Warned " + C.getFullName(tUser)) + "\n:page_facing_up: " + C.bold("Reason: ") + warningManager.getWarnReason(id) + "\n:id: **Warn ID** " + id)
            .build()).queue();
        if (warningManager.deleteWarning(id)) {
          e.reply("Deleted warning!");
        } else {
          e.reply("Warning could not be deleted!");
        }
      } else {
        e.replyError("Invalid Warning!");
      }
    } else {
      e.replyError(C.bold("Correct Usage:") + " ^" + name + " <warning ID>");
    }
  }
}
