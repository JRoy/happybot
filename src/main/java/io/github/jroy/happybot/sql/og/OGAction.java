package io.github.jroy.happybot.sql.og;

public class OGAction {

  private final OGActionType actionType;
  private final String userId;
  private final Integer pendingId;
  private final String pendingName;
  private final String pendingContent;

  public OGAction(OGActionType actionType, String userId, Integer pendingId, String pendingName, String pendingContent) {
    this.actionType = actionType;
    this.userId = userId;
    this.pendingId = pendingId;
    this.pendingName = pendingName;
    this.pendingContent = pendingContent;
  }

  public OGActionType getActionType() {
    return actionType;
  }

  public String getUserId() {
    return userId;
  }

  public Integer getPendingId() {
    return pendingId;
  }

  public String getPendingName() {
    return pendingName;
  }

  public String getPendingContent() {
    return pendingContent;
  }
}
