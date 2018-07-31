package io.github.jroy.happybot.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

  private static String prefix = "[HappyBot] ";
  private static String info = prefix + "[INFO] ";
  private static String warn = prefix + "[WARN] ";
  private static String error = prefix + "[ERROR] ";

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public Logger() throws IOException {
    System.out.println(info + "Loading Logger..");
    File oldlog = new File("latest.log");
    if (oldlog.exists()) {

      System.out.println(info + "Saving Old Log.");
      oldlog.renameTo(new File("logs/last.log"));
      oldlog.delete();
    }
    new File("latest.log").createNewFile();
    System.out.println(info + "Loaded Logger!");
  }

  public static void log(String output) {
    try {
      FileWriter writer = new FileWriter("latest.log", true);
      writer.write(prefix + output);
      writer.write(System.lineSeparator());
      writer.close();
    } catch (IOException ignored) {
    }
  }

  public static void info(String output) {
    System.out.println(info + output);
    log(info + output);
  }

  public static void warn(String output) {
    System.out.println(warn + output);
    log(warn + output);
  }

  public static void error(String output) {
    System.out.println(error + output);
    log(error + output);
  }

}
