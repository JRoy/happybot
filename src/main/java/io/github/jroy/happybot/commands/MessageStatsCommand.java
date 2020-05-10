package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.sql.MessageFactory;

public class MessageStatsCommand extends CommandBase {

  private final MessageFactory messageFactory;

  public MessageStatsCommand(MessageFactory messageFactory) {
    super("messagestats", null, "Gives statistics about messages from the MessageFactory.", CommandCategory.FUN);
    this.aliases = new String[]{"msgstats", "mstats"};
    this.messageFactory = messageFactory;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    e.getChannel().sendMessage(":information_source: Message Queue Stats:" +
        "\n**Welcome Messages:** " + messageFactory.getTotals(MessageFactory.MessageType.JOIN) +
        "\n**Quit Messages:** " + messageFactory.getTotals(MessageFactory.MessageType.LEAVE) +
        "\n**Warning Messages:** " + messageFactory.getTotals(MessageFactory.MessageType.WARN) +
        "\n**Level-Up Messages:** " + messageFactory.getTotals(MessageFactory.MessageType.LEVEL) +
        "\n**Update Start Messages:** " + messageFactory.getTotals(MessageFactory.MessageType.UPDATE_START) +
        "\n**Update End Messages:** " + messageFactory.getTotals(MessageFactory.MessageType.UPDATE_END)).queue();
  }
}
