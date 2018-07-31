package io.github.jroy.happybot.sql;

import io.github.jroy.happybot.Main;
import net.dv8tion.jda.core.entities.User;

public class ReportToken {

  private final int id;
  private final User reporter;
  private final User target;
  private final String channelId;
  private final String reason;
  private final User handler;
  private final String handleReason;
  private final int status;

  public ReportToken(int id, String reporterId, String targetId, String channelId, String reason, String handlerId, String handleReason, int status) {
    this.id = id;
    this.reporter = Main.getJda().getUserById(reporterId);
    this.target = Main.getJda().getUserById(targetId);
    this.channelId = channelId;
    this.reason = reason;
    this.handler = Main.getJda().getUserById(handlerId);
    this.handleReason = handleReason;
    this.status = status;
  }

  public int getId() {
    return id;
  }

  public User getReporter() {
    return reporter;
  }

  public User getTarget() {
    return target;
  }

  public String getChannelId() {
    return channelId;
  }

  public String getReason() {
    return reason;
  }

  public User getHandler() {
    return handler;
  }

  public String getHandleReason() {
    return handleReason;
  }

  public int getStatus() {
    return status;
  }
}
