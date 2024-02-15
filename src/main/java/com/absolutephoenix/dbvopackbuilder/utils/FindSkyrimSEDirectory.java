package com.absolutephoenix.dbvopackbuilder.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is designed to find the installation directory of Skyrim Special Edition
 * by looking through the Steam library folders.
 */
@SuppressWarnings("ALL")
public class FindSkyrimSEDirectory {

    /**
     * Retrieves the path to the Skyrim Special Edition Data folder.
     * It first attempts to get the Steam installation path and then checks
     * the library folders for the presence of Skyrim Special Edition.
     *
     * @return A string containing the path to the Skyrim Special Edition Data folder
     *         or null if it cannot be found.
     */
    public static String get(){
        String steamPath = getSteamInstallationPath();

        if (steamPath != null && !steamPath.isEmpty()) {
            return checkLibraryFolders(steamPath);
        } else {
            System.out.println("Steam installation not found.");
            return null;
        }
    }

    /**
     * Tries to locate the Steam installation directory by querying the Windows registry.
     *
     * @return The Steam installation path as a string or null if not found or in case of an error.
     */
    private static String getSteamInstallationPath() {
        try {
            // Querying for Steam's installation path from the registry
            Process process = new ProcessBuilder("reg", "query",
                    "HKCU\\Software\\Valve\\Steam", "/v", "SteamPath")
                    .redirectErrorStream(true)
                    .start();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().startsWith("SteamPath")) {
                        return line.split("REG_SZ")[1].trim();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Scans through Steam's library folders to find the installation path of Skyrim Special Edition.
     * It reads the 'libraryfolders.vdf' file which contains paths to different Steam libraries.
     *
     * @param steamPath The base installation path for Steam.
     * @return The path to Skyrim Special Edition Data folder as a string,
     *         or null if the folder cannot be found in any Steam library.
     */
    private static String checkLibraryFolders(String steamPath) {
        Path libraryVdfPath = Paths.get(steamPath, "steamapps", "libraryfolders.vdf");

        try (BufferedReader br = new BufferedReader(new FileReader(libraryVdfPath.toFile()))) {
            String line;
            Pattern p = Pattern.compile("\"path\"\\s+\"(.*?)\"");
            while ((line = br.readLine()) != null) {
                Matcher m = p.matcher(line);
                if (m.find()) {
                    String libraryPath = m.group(1).replace("\\\\", "\\");
                    Path potentialSkyrimSEDataPath = Paths.get(libraryPath, "steamapps", "common", "Skyrim Special Edition", "Data");
                    if (java.nio.file.Files.exists(potentialSkyrimSEDataPath)) {
                        return potentialSkyrimSEDataPath.toString();
                    }
                }
            }
        } catch (Exception e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
        System.out.println("Skyrim Special Edition Data folder not found in any Steam library.");
        return null;
    }
}
