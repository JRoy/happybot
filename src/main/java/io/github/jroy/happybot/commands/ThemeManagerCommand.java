package io.github.jroy.happybot.commands;

@SuppressWarnings("ignored")
public class ThemeManagerCommand /* extends Command*/ {

//    private DiscordThemerImpl themeManager;
//
//    public ThemeManagerCommand(DiscordThemerImpl themeManager) {
//        this.name = "thememngr";
//        this.help = "A command to manage themes.";
//        this.arguments = "<load/delete/reload> <link/theme name> <theme name>";
//        this.guildOnly = true;
//        this.category = new Category("Bot Management");
//        this.themeManager = themeManager;
//    }
//
//    @Override
//    protected void execute(CommandEvent e) {
//        if (C.hasRole(e.matchMember(), Roles.SUPER_ADMIN)) {
//            if (e.getArgs().isEmpty()) {
//                e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
//                return;
//            }
//            String[] args = e.getSplitArgs();
//            if (args[0].equalsIgnoreCase("load")) {
//                if (args.length != 3) {
//                    e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
//                    return;
//                }
//                try {
//                    new URL(args[1]);
//                } catch (MalformedURLException ignored) {
//                    e.replyError("Invalid URL");
//                    return;
//                }
//
//                C.dlFile(args[1], "themes/"+args[2]+".dat");
//
//                try {
//                    if (themeManager.validateTheme(args[2]+".dat", true)) {
//                        e.replySuccess("Loaded Theme: " + args[2]);
//                    }
//                    e.replyError("The provided theme file was invalid!");
//                    if (!new File(args[2] + ".dat").delete())
//                        e.replyError("Unable to delete file!");
//                } catch (FileNotFoundException e1) {
//                    e1.printStackTrace();
//                } catch (InvalidThemeFileException e1) {
//                    e.replyError("An InvalidThemeFileException was thrown when parsing your file:" + C.codeblock(e1.getMessage()));
//                    if (!new File("themes"+args[2]+".dat").delete())
//                        e.replyError("Unable to delete file!");
//                }
//            } else if (args[0].equalsIgnoreCase("delete")) {
//                if (args.length != 2) {
//                    e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
//                    return;
//                }
//                if (themeManager.getThemes().contains(args[1])) {
//                    try {
//                        themeManager.removeTheme(args[1]);
//                        e.replySuccess("Deleted Theme: "+args[1]);
//                        if (!new File(args[1] + ".dat").renameTo(new File("themes/deleted/"+args[1]+".dat")))
//                            e.replyError("Unable to move file!");
//                    } catch (ThemeNotFoundException e1) {
//                        e.replyError("The target theme was not found!");
//                    }
//                }
//            } else if (args[0].equalsIgnoreCase("reload")) {
//                if (args.length != 2) {
//                    e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
//                    return;
//                }
//                if (themeManager.getThemes().contains(args[1])) {
//                    try {
//                        themeManager.reloadTheme(args[1]);
//                        e.replySuccess("Reloaded theme!");
//                    } catch (ThemeNotFoundException | InvalidThemeFileException e1) {
//                        e.replyError("Error while reloading theme: " + e1.getMessage());
//                    }
//                } else {
//                    e.replyError("The target theme was not found!");
//                }
//            } else {
//                e.replyError(C.bold("Correct Usage:") + " ^" + name + " " + arguments);
//            }
//        } else {
//            e.replyError(C.permMsg(Roles.SUPER_ADMIN));
//        }
//    }
}
