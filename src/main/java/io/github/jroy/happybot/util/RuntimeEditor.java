package io.github.jroy.happybot.util;

public class RuntimeEditor {

  private static boolean allowSelfGilds = false;
  private static boolean evalOwnerOnly = true;
  private static boolean pingIssueClose = false;
  private static boolean filteringAdverts = true;
  private static boolean permittingWarningExposement = false;
  private static boolean allowEditOtherUserWarn = false;
  private static boolean allowStaffSubBypass = false;

  private static float gambleJackpot = 0;

  private static int gambleMax = 10000;

  public static boolean isAllowSelfGilds() {
    return allowSelfGilds;
  }

  public static void setAllowSelfGilds(boolean allowSelfGilds) {
    RuntimeEditor.allowSelfGilds = allowSelfGilds;
  }

  public static int getGambleMax() {
    return gambleMax;
  }

  public static void setGambleMax(int gambleMax) {
    RuntimeEditor.gambleMax = gambleMax;
  }

  public static boolean isEvalOwnerOnly() {
    return evalOwnerOnly;
  }

  public static void setEvalOwnerOnly(boolean evalOwnerOnly) {
    RuntimeEditor.evalOwnerOnly = evalOwnerOnly;
  }

  public static boolean isPingIssueClose() {
    return pingIssueClose;
  }

  public static void setPingIssueClose(boolean pingIssueClose) {
    RuntimeEditor.pingIssueClose = pingIssueClose;
  }

  public static boolean isFilteringAdverts() {
    return filteringAdverts;
  }

  public static void setFilteringAdverts(boolean filteringAdverts) {
    RuntimeEditor.filteringAdverts = filteringAdverts;
  }

  public static boolean isPermittingWarningExposement() {
    return permittingWarningExposement;
  }

  public static void setPermittingWarningExposement(boolean permittingWarningExposement) {
    RuntimeEditor.permittingWarningExposement = permittingWarningExposement;
  }

  public static boolean isAllowEditOtherUserWarn() {
    return allowEditOtherUserWarn;
  }

  public static void setAllowEditOtherUserWarn(boolean allowEditOtherUserWarn) {
    RuntimeEditor.allowEditOtherUserWarn = allowEditOtherUserWarn;
  }

  public static float getGambleJackpot() {
    return gambleJackpot;
  }

  public static void setGambleJackpot(float gambleJackpot) {
    RuntimeEditor.gambleJackpot = gambleJackpot;
  }

  public static boolean isAllowStaffSubBypass() {
    return allowStaffSubBypass;
  }

  public static void setAllowStaffSubBypass(boolean allowStaffSubBypass) {
    RuntimeEditor.allowStaffSubBypass = allowStaffSubBypass;
  }
}
