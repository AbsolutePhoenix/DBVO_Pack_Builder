package com.absolutephoenix.dbvopackbuilder.utils;

import com.absolutephoenix.dbvopackbuilder.config.ConfigManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class PackBuild {

    public static void createModpackJson(){
        File targetDir = new File("build/loose/" + "--BASE--" + "/DragonbornVoiceOver/voice_packs/").getAbsoluteFile();
        Path targetFile = new File(targetDir,  ConfigManager.getSetting().getPackID() + ".json").toPath();
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        if(!targetFile.toFile().exists()) {
            String fileData = "{\n" +
                    "\t\"voice_pack_name\":\"" + ConfigManager.getSetting().getPackName() + "\",\n" +
                    "\t\"voice_pack_id\":\"" + ConfigManager.getSetting().getPackID() + "\"\n" +
                    "}";

            try {
                Files.writeString(targetFile, fileData);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    public static void copyFileToPackFolder(String fileName, String modName) {
        File fuzDir = new File("staging/fuz/").getAbsoluteFile();
        Path fuzPath = new File(fuzDir, fileName + ".fuz").toPath();

        File targetDir = new File("build/loose/" + modName + "/Sound/DBVO/" + ConfigManager.getSetting().getPackID() + "/").getAbsoluteFile();
        Path targetPath = new File(targetDir, fileName + ".fuz").toPath();

        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        if (!Files.exists(targetPath)) {
            LogHelper.grayInfo("COPYING TO PACK");
            try {
                Files.copy(fuzPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void bsaPack(String modName){
        String inputDir = new File("build/loose/" + modName).getAbsoluteFile().getAbsolutePath();
        String outputBSA = new File("build/bsa/" + modName + "/" + ConfigManager.getSetting().getPackVoiceName() + " - " + modName + ".bsa").getAbsolutePath();
        String bsarch = new File("bin/bsarch.exe").getAbsolutePath();

        LogHelper.info("PACKING TO BSA");

        if(!new File(outputBSA).getParentFile().exists()){
            new File(outputBSA).getParentFile().mkdirs();
        }

        ProcessBuilder processBuilder = new ProcessBuilder(
                bsarch,
                "pack",
                inputDir,
                outputBSA,
                "-sse",
                "-mt"
        );

        try {
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            // Capture standard error
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.err.println(line);
                }
            }

            int exitCode = process.waitFor();

            if (exitCode == 0) {
            } else {
                System.out.println("BSA file generation failed with exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }



        String resourceFilePath = "/espdata/base.esp";
        Path espPath = new File("build/bsa/" + modName + "/" + ConfigManager.getSetting().getPackVoiceName() + " - " + modName + ".esp").toPath();

        if (!Files.exists(espPath.getParent())) {
            try {
                Files.createDirectories(espPath.getParent());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (InputStream inputStream = PackBuild.class.getResourceAsStream(resourceFilePath)) {
            if (inputStream == null) {
                throw new IOException("Resource file not found: " + resourceFilePath);
            }

            // Copy the resource to the target location
            Files.copy(inputStream, espPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
