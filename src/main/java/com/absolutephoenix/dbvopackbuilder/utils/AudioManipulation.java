package com.absolutephoenix.dbvopackbuilder.utils;

import com.absolutephoenix.dbvopackbuilder.config.ConfigManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AudioManipulation {
    public static void convertMP3ToWAV(String fileName) {
        String ffmpegExecutablePath = new File ("bin/ffmpeg").getAbsolutePath(); // assuming ffmpeg binary is in the working directory
        File mp3Dir = new File("staging/mp3/").getAbsoluteFile();
        Path mp3FilePath = new File(mp3Dir, fileName + ".mp3").toPath();
        // Define the path to the temp folder inside the working directory
        File wavDir = new File("staging/wav/").getAbsoluteFile();
        Path wavePath = new File(wavDir, fileName + ".wav").toPath();

        if(!wavePath.toFile().exists()) {
            LogHelper.grayInfo("CONVERTING TO WAV");
            if(!wavDir.exists())
                wavDir.mkdirs();
            // Check if MP3 file exists
            if (!Files.exists(mp3FilePath)) {
                System.out.println("MP3 file does not exist: " + mp3FilePath);
                return;
            }

            // Build the FFmpeg command
            ProcessBuilder processBuilder = new ProcessBuilder(
                    ffmpegExecutablePath,
                    "-i", mp3FilePath.toString(),
                    "-acodec", "pcm_s16le",
                    "-ar", "44100",
                    "-ac", "1",
                    wavePath.toString()
            );

            try {
                Process process = processBuilder.start();
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    System.out.println("Conversion failed with exit code: " + exitCode);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public static void convertWaveToXMW(String fileName) {
        File wmxDir = new File("staging/xmw/").getAbsoluteFile();
        Path wmxPath = new File(wmxDir, fileName + ".xmw").toPath();

        File wavDir = new File("staging/wav/").getAbsoluteFile();
        Path wavePath = new File(wavDir, fileName + ".wav").toPath();
        // Path to the xWMAEncode executable - assuming it's in the current working directory
        String encoderPath = new File("bin/xWMAEncode.exe").getAbsolutePath();

        // Define the path to the temp folder inside the working directory

        // Ensure the temp directory exists
        if (!wmxDir.exists() && !wmxDir.mkdirs()) {
            System.out.println("Could not create temp directory.");
            return;
        }

        if(!wmxPath.toFile().exists()) {
            LogHelper.grayInfo("CONVERTING TO XMW");
            // Construct the full path for the input WAV file in the temp directory
            File inputWaveFile = wavePath.toFile();
            // Check if the input file exists
            if (!inputWaveFile.exists()) {
                System.out.println("The input WAV file does not exist: " + inputWaveFile.getAbsolutePath());
                return;
            }

            // Generate the output file path in the temp directory

            String outputFilePath = wmxPath.toString();

            // Construct the command
            String[] command = {encoderPath, inputWaveFile.getAbsolutePath(), outputFilePath};

            try {
                // Execute the command
                Process process = new ProcessBuilder(command).start();

                // Wait for the process to complete
                int exitCode = process.waitFor();

                // Check if the command was executed successfully
                if (exitCode == 0) {
                } else {
                    System.out.println("Conversion failed with exit code " + exitCode);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void convertXwmAndLipToFuz(String fileName) {
        File fuzDir = new File("staging/fuz/").getAbsoluteFile();
        Path fuzPath = new File(fuzDir, fileName + ".fuz").toPath();

        File wmxDir = new File("staging/xmw/").getAbsoluteFile();
        Path xmwPath = new File(wmxDir, fileName + ".xmw").toPath();
        File lipDir = new File("staging/lip/").getAbsoluteFile();
        Path lipPath = new File(lipDir, fileName + ".lip").toPath();

        // Path to the BmlFuzEncode executable - assuming it's in the current working directory
        String encoderPath = new File("./bin/BmlFuzEncode.exe").getAbsolutePath(); // Relative path to the encoder

        // Define the path to the temp folder inside the working directory

        // Ensure the temp directory exists
        if (!fuzDir.exists() && !fuzDir.mkdirs()) {
            System.out.println("Could not create temp directory.");
            return;
        }

        if(!fuzPath.toFile().exists()) {
            LogHelper.grayInfo("CONVERTING TO FUZ");
            // Construct the full paths for the xwm and lip files in the temp directory

            // Check if the input files exist
            if (!xmwPath.toFile().exists() || !lipPath.toFile().exists()) {
                System.out.println("One or more input files do not exist in the temp directory.");
                return;
            }

            // Generate the output file name and path in the temp directory

            // Construct the command
            ProcessBuilder processBuilder;
            if(ConfigManager.getSetting().getPackGenerateLip().equals("true")) {
                processBuilder = new ProcessBuilder(encoderPath, fuzPath.toString(), xmwPath.toString(), lipPath.toString());
            }else{
                processBuilder = new ProcessBuilder(encoderPath, fuzPath.toString(), xmwPath.toString(), "-nolip");
            }
            processBuilder.redirectErrorStream(true); // Redirect error stream to the output stream

            try {
                // Execute the command
                Process process = processBuilder.start();

                // Wait for the process to complete
                int exitCode = process.waitFor();

                // Check if the command was executed successfully
                if (exitCode == 0) {
                } else {
                    System.err.println("Conversion failed with exit code " + exitCode);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
