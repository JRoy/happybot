package io.github.jroy.happybot.util;

import lombok.Getter;
import lombok.Setter;

public class RuntimeEditor {
  @Getter
  @Setter
  private static boolean allowSelfGilds = false;
  @Getter
  @Setter
  private static boolean evalOwnerOnly = true;
  @Getter
  @Setter
  private static boolean pingIssueClose = false;
  @Getter
  @Setter
  private static boolean filteringAdverts = true;
  @Getter
  @Setter
  private static boolean permittingWarningExposement = false;
  @Getter
  @Setter
  private static boolean allowEditOtherUserWarn = false;
  @Getter
  @Setter
  private static boolean allowStaffSubBypass = false;

  @Getter
  @Setter
  private static float gambleJackpot = 0;
  @Getter
  @Setter
  private static int gambleMax = 10000;
}
