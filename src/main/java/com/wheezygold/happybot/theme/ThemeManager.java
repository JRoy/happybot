package com.wheezygold.happybot.theme;

import com.wheezygold.happybot.theme.exceptions.InvalidThemeFileException;
import com.wheezygold.happybot.theme.exceptions.ThemeNotFoundException;
import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Logger;
import net.dv8tion.jda.core.entities.Icon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ThemeManager {

    private HashMap<String, HashMap<String, String>> themeData = new HashMap<>();
    private HashMap<String, HashMap<String, String>> themeMetaData = new HashMap<>();
    private ThemeManager instance;

    public ThemeManager() {
        instance = this;
        loadThemes();
    }

    private void loadThemes() {
        File[] rawThemes = new File("themes/data/").listFiles();

        Logger.info("Loading Theme Manager...");

        if (rawThemes == null || rawThemes.length == 0) {
            Logger.warn("No themes have been found!");
            return;
        }

        Logger.info("Parsing Theme Files...");

        for (File theme : rawThemes) {
            if (!theme.isDirectory()) {
                parseTheme(theme, theme.getName());
            }
        }


        Logger.info("Loaded Themes:");
        for (Object themeLister : getThemes().toArray()) {
            Logger.info("- " + themeLister);
        }

    }

    /**
     * @return True if the theme was parsed successfully
     */
    private boolean parseTheme(File file, String themeName) {
        try {
            if (validateTheme(themeName, false)) {
                parseThemeFileMeta(themeName, new Scanner(file));
                parseThemeFile(themeName, new Scanner(file));
                return true;
            } else {
                Logger.error("Error Parsing Theme File: " + themeName);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidThemeFileException e) {
            Logger.error(e.getMessage());
        }
        return false;
    }

    private void parseThemeFile(String themeName, Scanner themeFileScanner) {
        String editThemeName = themeName.split("[.]")[0];
        themeData.put(editThemeName, new HashMap<>());

        while (themeFileScanner.hasNextLine()) {
            String[] lineTokens = themeFileScanner.nextLine().split("[:]");
            if (!lineTokens[0].equalsIgnoreCase("MetaData")) {
                themeData.get(editThemeName).put(lineTokens[0], lineTokens[1]);
            }
        }
    }

    private void parseThemeFileMeta(String themeName, Scanner themeFileScanner) {
        String editThemeName = themeName.split("[.]")[0];
        themeMetaData.put(editThemeName, new HashMap<>());

        while (themeFileScanner.hasNextLine()) {
            String[] lineTokens = themeFileScanner.nextLine().split("[:]");
            if (lineTokens[0].equalsIgnoreCase("MetaData")) {
                themeMetaData.get(editThemeName).put(lineTokens[1], lineTokens[2]);
            }
        }
    }

    public boolean validateTheme(String themeName, boolean parse) throws FileNotFoundException, InvalidThemeFileException {
        String editThemeName = themeName;
        if (!editThemeName.contains(".dat"))
            editThemeName = editThemeName + ".dat";
        Scanner validateScanner = new Scanner(new File("themes/data/" + editThemeName));
        List<String> metaTokens = new ArrayList<>();
        while (validateScanner.hasNextLine()) {
            String curLine = validateScanner.nextLine();
            /*
            MetaData      :    title    :     Happyheart
             ^^^^^^             ^^^            ^^^^^^
             Token 1           Token 2         Token 3
             */
            String[] lineTokens = curLine.split("[:]");
            if (lineTokens[0].equalsIgnoreCase("MetaData")) {
                if (lineTokens.length != 3)
                    throw new InvalidThemeFileException("Unparseable line in \"" + editThemeName + "\": " + curLine);
                metaTokens.add(lineTokens[1]);
            } else {
                if (lineTokens.length != 2) {
                    throw new InvalidThemeFileException("Unparseable line in \"" + editThemeName + "\": " + curLine);
                }
            }
        }
        if (metaTokens.contains("title") && metaTokens.contains("icon") && metaTokens.contains("nickname") && metaTokens.contains("name")) {
            if (parse) {
                File parseFile = new File("themes/data/" + editThemeName);
                parseThemeFile(editThemeName, new Scanner(parseFile));
                parseThemeFileMeta(editThemeName, new Scanner(parseFile));
            }
            return true;
        }
        return false;
    }

    public ArrayList<String> getThemes() {
        ArrayList<String> array = new ArrayList<>();
        for (HashMap.Entry<String, HashMap<String, String>> curEntry : themeData.entrySet()) {
            array.add(curEntry.getKey());
        }
        return array;
    }

    public ThemeToken asToken(String themeName) throws ThemeNotFoundException {
        return new ThemeToken(instance, themeName);
    }

    public void removeTheme(String themeName) throws ThemeNotFoundException {
        if (themeData.get(themeName) == null)
            throw new ThemeNotFoundException("Theme: " + themeName + " was not found in the loaded theme data.");
        if (themeMetaData.get(themeName) == null)
            throw new ThemeNotFoundException("Theme: " + themeName + " was not found in the loaded theme meta data.");
        themeData.remove(themeName);
        themeMetaData.remove(themeName);
    }

    public void reloadTheme(String themeName) throws ThemeNotFoundException, InvalidThemeFileException {
        if (!themeData.containsKey(themeName))
            throw new ThemeNotFoundException("Theme: " + themeName + " was not found in the loaded theme data.");

        File[] files = new File("themes/data/").listFiles();
        if (files == null || files.length == 0) {
            Logger.warn("No themes have been found!");
            return;
        }

        Optional<File> optionalTheme = Arrays.stream(files).filter(file -> file.getName().equalsIgnoreCase(themeName + ".dat")).findAny();
        if (!optionalTheme.isPresent())
            throw new ThemeNotFoundException("Theme file: " + themeName + " was not found.");

        File theme = optionalTheme.get();
        if (!parseTheme(theme, themeName))
            throw new InvalidThemeFileException("Unparseable Theme");
    }

    protected HashMap<String, String> getThemeData(String themeName) throws ThemeNotFoundException {
        if (themeData.get(themeName) == null)
            throw new ThemeNotFoundException("Theme: " + themeName + " was not found in the loaded theme data.");
        return themeData.get(themeName);
    }

    protected HashMap<String, String> getThemeMetaData(String themeName) throws ThemeNotFoundException {
        if (themeMetaData.get(themeName) == null)
            throw new ThemeNotFoundException("Theme: " + themeName + " was not found in the loaded theme data.");
        return themeMetaData.get(themeName);
    }

    public void switchTheme(String themeName) {
        new Thread(new SwitchTheme(themeName, this)).start();
    }

    private static class SwitchTheme implements Runnable {

        private String themeName;
        private ThemeManager themeManager;

        SwitchTheme(String themeName, ThemeManager themeManager) {
            this.themeName = themeName;
            this.themeManager = themeManager;
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        public void run() {
            HashMap<String, String> roleToken = null;
            HashMap<String, String> roleMetaToken = null;

            try {
                roleToken = themeManager.getThemeData(themeName);
                roleMetaToken = themeManager.getThemeMetaData(themeName);
            } catch (ThemeNotFoundException e) {
                e.printStackTrace();
            }

            try {
                C.getGuild().getManager().setIcon(Icon.from(new File("themes/icons/" + roleMetaToken.get("icon") + ".png"))).queue();
                C.getGuild().getManager().setName(roleMetaToken.get("title")).queue();
                C.getGuildCtrl().setNickname(C.getGuild().getMemberById("354736186516045835"), roleMetaToken.get("nickname")).queue();
                for (HashMap.Entry<String, String> entry : roleToken.entrySet()) {
                    C.getGuild().getRoleById(entry.getKey()).getManager().setName(entry.getValue()).queue();
                    TimeUnit.MILLISECONDS.sleep(100);
                }
                C.getGuild().getManager().setName(roleMetaToken.get("title")).queue();
                Logger.warn("Done Switching");
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}