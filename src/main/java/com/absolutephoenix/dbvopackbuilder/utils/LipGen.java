package com.absolutephoenix.dbvopackbuilder.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LipGen {
    public static void generate(String fileName, String sentence) {
        LogHelper.debug("Starting lip file generation for: " + fileName);
        Path lipPath = Paths.get("staging/lip", fileName + ".lip");
        Path resamplePath = Paths.get("staging/resample", fileName + ".wav");
        Path wavePath = Paths.get("staging/wav", fileName + ".wav");

        if (!lipPath.toFile().exists()) {
            LogHelper.grayInfo("Generating lip data for " + sentence);
            ensureDirectoryExists(lipPath.getParent());
            ensureDirectoryExists(resamplePath.getParent());

            try {
                Thread.sleep(10);
                LogHelper.debug("Waited 10ms for external tool synchronization");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LogHelper.error("Interrupted while waiting: " + e.getMessage());
                throw new RuntimeException("Interrupted while waiting for external tool", e);
            }

            String faceFxWrapperPath = new File("bin/FaceFXWrapper.exe").getAbsolutePath();
            String[] command = {
                    faceFxWrapperPath,
                    "skyrim",
                    "USEnglish",
                    new File("bin/FonixData.cdf").getAbsolutePath(),
                    wavePath.toString(),
                    resamplePath.toString(),
                    lipPath.toString(),
                    sentence.replace("\"", "")
            };

            LogHelper.debug("Running external process: " + String.join(" ", command));
            runProcess(new ProcessBuilder(command));
        } else {
            LogHelper.debug("Lip file already exists, skipping generation for: " + fileName);
        }
        LogHelper.debug("Completed lip file generation for: " + fileName);
    }

    private static void ensureDirectoryExists(Path dir) {
        if (!dir.toFile().exists()) {
            //noinspection ResultOfMethodCallIgnored
            dir.toFile().mkdirs();
        }
    }

    private static void runProcess(ProcessBuilder processBuilder) {
        try {
            Process process = processBuilder.start();
            handleProcessOutput(process);
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.out.println("LIP file generation failed with exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static void handleProcessOutput(Process process) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.err.println(line);
            }
        }
    }
}
