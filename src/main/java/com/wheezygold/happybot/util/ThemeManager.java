package com.wheezygold.happybot.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class ThemeManager {

	private HashMap<String, HashMap<String, String>> themeData = new HashMap<>();

	public ThemeManager() {
		loadThemes();
	}

	private void loadThemes() {
		File[] rawThemes = new File("themes/").listFiles();

		if (rawThemes == null) {
			C.log("No themes have been found!");
			return;
		}

		for (File theme : rawThemes) {
			try {
				Scanner scanner = new Scanner(theme);
				parseThemeFile(theme.getName(), scanner);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	private void parseThemeFile(String themeName, Scanner themeFileScanner) {
		// (Techno-coder) Wheezy you idiot if you put this inside the loop its going to clear the HashMap each time
		themeData.put(themeName, new HashMap<>());
		while (themeFileScanner.hasNextLine()) {
			String[] lineTokens = themeFileScanner.nextLine().split("[:]");
			themeData.get(themeName).put(lineTokens[0], lineTokens[1]);
		}
	}

	// (Techno-coder) Wheezy you are an idiot dont make getters for the raw data; this is meant to be the manager class
//    public HashMap<String, HashMap<String, String>> getThemeData() {
//        return themeData;
//    }

}
