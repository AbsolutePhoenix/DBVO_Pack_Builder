package com.absolutephoenix.dbvopackbuilder.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LipGen {
    public static void generate(String fileName, String sentence) {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Define the path to the temp folder inside the working directory
        File lipDir = new File("staging/lip/").getAbsoluteFile();
        Path lipPath = new File(lipDir, fileName + ".lip").toPath();

        File resampleDir = new File("staging/resample/").getAbsoluteFile();
        Path resamplePath = new File(resampleDir, fileName + ".wav").toPath();

        File wavDir = new File("staging/wav/").getAbsoluteFile();
        Path wavePath = new File(wavDir, fileName + ".wav").toPath();

        if (!lipPath.toFile().exists()) {
            LogHelper.grayInfo("GENERATING LIP DATA");

            // Ensure the temp directory exists
            if (!lipDir.exists()) {
                lipDir.mkdirs();
                return;
            }
            if (!resampleDir.exists()) {
                resampleDir.mkdirs();
                return;
            }
            // Assuming the wavFileName includes the .wav extension
            // Construct the file path for the existing WAV file in the temp directory
            String wavFilePath = wavePath.toString();

            // Generate the name for the LIP file based on the WAV file
            String lipFilePath = lipPath.toString();
            String resampledWavPath = resamplePath.toString();

            String faceFxWrapperPath = new File("bin/FaceFXWrapper.exe").getAbsolutePath();
            String game = "skyrim";
            String language = "USEnglish";
            String fonixDataPath = new File("bin/FonixData.cdf").getAbsolutePath(); // Assuming this is in the current directory

            ProcessBuilder processBuilder = new ProcessBuilder(
                    faceFxWrapperPath,
                    game,
                    language,
                    fonixDataPath,
                    wavFilePath,
                    resampledWavPath,
                    lipFilePath,
                    sentence.replace("\"", "")
            );

            try {
                Process process = processBuilder.start();

                // Capture standard output
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
                    System.out.println("LIP file generation failed with exit code: " + exitCode);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
