package com.absolutephoenix.dbvopackbuilder.utils;

import com.absolutephoenix.dbvopackbuilder.config.ConfigManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class PackBuild {

    public static void createModpackJson() {
        LogHelper.debug("Starting creation of modpack JSON");
        Path targetDir = Paths.get("build", "loose", "--BASE--", "DragonbornVoiceOver", "voice_packs");
        Path targetFile = targetDir.resolve(ConfigManager.getSetting().getPackID() + ".json");

        try {
            Files.createDirectories(targetDir);
            LogHelper.debug("Directory created: " + targetDir);

            if (Files.notExists(targetFile)) {
                String fileData = String.format("{\n\t\"voice_pack_name\":\"%s\",\n\t\"voice_pack_id\":\"%s\"\n}",
                        ConfigManager.getSetting().getPackName(),
                        ConfigManager.getSetting().getPackID());

                Files.writeString(targetFile, fileData);
                LogHelper.debug("Modpack JSON created: " + targetFile);
            }
        } catch (IOException e) {
            LogHelper.error("Error creating modpack JSON: " + e.getMessage());
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    public static void copyFileToPackFolder(String fileName, String modName) {
        LogHelper.debug("Copying file to pack folder: " + fileName);
        Path fuzPath = Paths.get("staging", "fuz", fileName + ".fuz");

        Path targetDir = Paths.get("build", "loose", modName, "Sound", "DBVO", ConfigManager.getSetting().getPackID());
        Path targetPath = targetDir.resolve(fileName + ".fuz");
        Path targetDir2 = Paths.get("build", "loose", "AIO", "Sound", "DBVO", ConfigManager.getSetting().getPackID());
        Path targetPath2 = targetDir2.resolve(fileName + ".fuz");

        try {
            Files.createDirectories(targetDir);
            LogHelper.debug("Directory created: " + targetDir);
            LogHelper.grayInfo("Copying " + targetPath + " to " + modName + " folder.");
            Files.copy(fuzPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            LogHelper.debug("File copied successfully: " + targetPath);

            Files.createDirectories(targetDir2);
            LogHelper.debug("Directory created: " + targetDir2);
            LogHelper.grayInfo("Copying " + targetPath2 + " to " + "AIO" + " folder.");
            Files.copy(fuzPath, targetPath2, StandardCopyOption.REPLACE_EXISTING);
            LogHelper.debug("File copied successfully: " + targetPath2);


        } catch (IOException e) {
            LogHelper.error("Error copying file to pack folder: " + e.getMessage());
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    public static void bsaPack(String modName) {
        LogHelper.debug("Starting BSA packing for mod: " + modName);
        String inputDir = new File("build/loose", modName).getAbsolutePath();
        File outputBSAFile = new File("build/bsa", modName + "/" + ConfigManager.getSetting().getPackVoiceName() + " - " + modName + ".bsa");
        String outputBSA = outputBSAFile.getAbsolutePath();
        String bsarch = new File("bin/bsarch.exe").getAbsolutePath();

        LogHelper.info("PACKING TO BSA");

        //noinspection ResultOfMethodCallIgnored
        outputBSAFile.getParentFile().mkdirs();

        ProcessBuilder processBuilder = new ProcessBuilder(bsarch, "pack", inputDir, outputBSA, "-sse", "-mt");
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            LogHelper.debug("BSA packing process started");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                reader.lines().forEach(line -> LogHelper.debug("BSA Pack Output: " + line));
            }

            if (process.waitFor() != 0) {
                LogHelper.error("BSA file generation failed with exit code: " + process.exitValue());
            } else {
                LogHelper.debug("BSA file generation completed successfully");
            }
        } catch (IOException | InterruptedException e) {
            LogHelper.error("Error during BSA packing: " + e.getMessage());
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }

        // Copying ESP file
        String resourceFilePath = "/espdata/base.esp";
        Path espPath = outputBSAFile.toPath().resolveSibling(ConfigManager.getSetting().getPackVoiceName() + " - " + modName + ".esp");

        try (InputStream inputStream = PackBuild.class.getResourceAsStream(resourceFilePath)) {
            if (inputStream == null) {
                LogHelper.error("Resource file not found: " + resourceFilePath);
                throw new IOException("Resource file not found: " + resourceFilePath);
            }
            Files.createDirectories(espPath.getParent());
            Files.copy(inputStream, espPath, StandardCopyOption.REPLACE_EXISTING);
            LogHelper.debug("ESP file copied successfully: " + espPath);
        } catch (IOException e) {
            LogHelper.error("Error copying ESP file: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
