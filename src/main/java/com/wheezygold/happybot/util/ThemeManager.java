package com.wheezygold.happybot.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class ThemeManager {

    private HashMap<String, HashMap<String, String>> themeData = new HashMap<>();

    public ThemeManager() {
        File[] rawThemes = new File("themes/").listFiles();

        if (rawThemes != null) {
            for (File curTheme : rawThemes) {
                //Create a HashMap of our themes and theme ids.
                Scanner target = null;
                String fileDisplay = curTheme.getName().split("[.]")[0];
                try {
                    target = new Scanner(new File("themes/" + curTheme.getName()));

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (target != null) {
                    while (target.hasNextLine()) {
                        String[] parsedLine = target.nextLine().split("[:]");
                        themeData.put(fileDisplay, new HashMap<String, String>());
                        themeData.get(fileDisplay).put(parsedLine[0], parsedLine[1]);
                    }
                }
            }
        } else {
            C.log("No themes have been found!");
        }

    }

    public HashMap<String, HashMap<String, String>> getThemeData() {
        return themeData;
    }

}
