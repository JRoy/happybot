package com.wheezygold.happybot.theme;

import com.wheezygold.happybot.theme.exceptions.ThemeNotFoundException;
import com.wheezygold.happybot.util.C;
import net.dv8tion.jda.core.entities.Icon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class ThemeManager {

	private HashMap<String, HashMap<String, String>> themeData = new HashMap<>();
	private HashMap<String, HashMap<String, String>> themeMetaData = new HashMap<>();

	public ThemeManager() {
		loadThemes();
	}

	private void loadThemes() {
		File[] rawThemes = new File("themes/").listFiles();

		if (rawThemes == null || rawThemes.length == 0) {
			C.log("No themes have been found!");
			return;
		}

		C.log("Loaded Theme Files:");

		for (File theme : rawThemes) {
		    C.log("- " + theme.getName().split("[.]")[0]);
        }

		C.log("Parsing Theme Files...");

		for (File theme : rawThemes) {
			try {
                parseThemeFileMeta(theme.getName(), new Scanner(theme));
				parseThemeFile(theme.getName(), new Scanner(theme));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	private void parseThemeFile(String themeName, Scanner themeFileScanner) {
		// (Techno-coder) Wheezy you idiot if you put this inside the loop its going to clear the HashMap each time
        themeName = themeName.split("[.]")[0];
		themeData.put(themeName, new HashMap<>());

		while (themeFileScanner.hasNextLine()) {
			String[] lineTokens = themeFileScanner.nextLine().split("[:]");
			if (!lineTokens[0].equalsIgnoreCase("MetaData")) {
                themeData.get(themeName).put(lineTokens[0], lineTokens[1]);
            }
		}
	}

	private void parseThemeFileMeta(String themeName, Scanner themeFileScanner) {
        themeName = themeName.split("[.]")[0];
        themeMetaData.put(themeName, new HashMap<>());

        while (themeFileScanner.hasNextLine()) {
            String[] lineTokens = themeFileScanner.nextLine().split("[:]");
            if (lineTokens[0].equalsIgnoreCase("MetaData")) {
                themeMetaData.get(themeName).put(lineTokens[1], lineTokens[2]);
            }
        }
    }

	public ArrayList<String> getThemes() {
	    ArrayList<String> array = new ArrayList<>();
	    for (HashMap.Entry<String, HashMap<String, String>> curEntry : themeData.entrySet()) {
	        array.add(curEntry.getKey());
        }
	    return array;
    }

    public HashMap<String, String> getThemeData(String themeName) throws ThemeNotFoundException {
	    if (themeData.get(themeName) == null)
	        throw new ThemeNotFoundException("Theme: " + themeName + " was not found in the loaded theme data.");
        return themeData.get(themeName);
    }

    public HashMap<String, String> getThemeMetaData(String themeName) throws ThemeNotFoundException {
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
                C.getGuild().getManager().setIcon(Icon.from(new File(roleMetaToken.get("icon") + ".png"))).queue();
                C.getGuild().getManager().setName(roleMetaToken.get("title")).complete();
                C.getGuildCtrl().setNickname(C.getGuild().getMemberById("354736186516045835"), roleMetaToken.get("nickname")).complete();
                for (HashMap.Entry<String, String> entry : roleToken.entrySet()) {
                    C.getGuild().getRoleById(entry.getKey()).getManager().setName(entry.getValue()).queue();
                    TimeUnit.MILLISECONDS.sleep(500);
                }
                C.getGuild().getManager().setName(roleMetaToken.get("title")).complete();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
