package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Member;

public class MemberCountCommand extends CommandBase {

  public MemberCountCommand() {
    super("membercount", null, "Returns counts of members in the server.", CommandCategory.GENERAL);
    this.aliases = new String[]{"usercount", "servercount"};
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    int count = 0;
    int online = 0;
    int bot = 0;
    for (Member curMember : e.getGuild().getMembers()) {
      count++;
      if (!curMember.getOnlineStatus().equals(OnlineStatus.OFFLINE)) {
        online++;
      }
      if (curMember.getUser().isBot()) {
        bot++;
      }
    }

    e.reply(new EmbedBuilder()
        .setTitle("Member Count")
        .addField("Members", String.valueOf(count), true)
        .addField("Online", String.valueOf(online), true)
        .addField("Humans", String.valueOf(count - bot), true)
        .addField("Bots", String.valueOf(bot), true)
        .build());
  }
}
