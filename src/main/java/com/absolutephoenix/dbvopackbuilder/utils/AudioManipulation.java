package com.absolutephoenix.dbvopackbuilder.utils;

import com.absolutephoenix.dbvopackbuilder.config.ConfigManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AudioManipulation {

    private static void createDirectoryIfNotExists(String directoryPath) {
        LogHelper.debug("Checking directory: " + directoryPath);
        File dir = new File(directoryPath);
        if (!dir.exists()) {
            LogHelper.verbose("Creating directory: " + directoryPath);
            if (!dir.mkdirs()) {
                LogHelper.error("Could not create directory: " + directoryPath);
            }
        }
    }

    public static void convertMP3ToWAV(String fileName) {
        LogHelper.performance("Starting MP3 to WAV conversion for: " + fileName);
        String ffmpegExecutablePath = new File("bin/ffmpeg").getAbsolutePath();
        Path mp3FilePath = Paths.get("staging/mp3/", fileName + ".mp3");
        Path wavePath = Paths.get("staging/wav/", fileName + ".wav");

        if (Files.exists(wavePath)) {
            LogHelper.grayInfo("WAV file already exists, skipping conversion for: " + fileName);
            return;
        }

        createDirectoryIfNotExists(wavePath.getParent().toString());

        if (!Files.exists(mp3FilePath)) {
            LogHelper.error("MP3 file does not exist: " + mp3FilePath);
            return;
        }

        executeProcess(new String[]{ffmpegExecutablePath, "-i", mp3FilePath.toString(), "-acodec", "pcm_s16le", "-ar", "44100", "-ac", "1", wavePath.toString()}, "Convert MP3 to WAV");
    }

    public static void convertWaveToXMW(String fileName) {
        LogHelper.performance("Starting WAV to XWM conversion for: " + fileName);
        Path wmxPath = Paths.get("staging/xwm/", fileName + ".xwm");
        Path wavePath = Paths.get("staging/wav/", fileName + ".wav");
        String encoderPath = new File("bin/xWMAEncode.exe").getAbsolutePath();

        if (Files.exists(wmxPath)) {
            LogHelper.grayInfo("XWM file already exists, skipping conversion for: " + fileName);
            return;
        }

        createDirectoryIfNotExists(wmxPath.getParent().toString());

        if (!Files.exists(wavePath)) {
            LogHelper.error("WAV file does not exist: " + wavePath);
            return;
        }

        executeProcess(new String[]{encoderPath, wavePath.toString(), wmxPath.toString()}, "Convert WAV to XWM");
    }

    public static void convertXwmAndLipToFuz(String fileName) {
        LogHelper.performance("Starting XWM and LIP to FUZ conversion for: " + fileName);
        Path fuzPath = Paths.get("staging/fuz/", fileName + ".fuz");
        Path xwmPath = Paths.get("staging/xwm/", fileName + ".xwm");
        Path lipPath = Paths.get("staging/lip/", fileName + ".lip");
        String encoderPath = new File("bin/BmlFuzEncode.exe").getAbsolutePath();

        if (Files.exists(fuzPath)) {
            LogHelper.grayInfo("FUZ file already exists, skipping conversion for: " + fileName);
            return;
        }

        createDirectoryIfNotExists(fuzPath.getParent().toString());

        if (!Files.exists(xwmPath) || (!Files.exists(lipPath) && ConfigManager.getSetting().getPackGenerateLip().equals("true"))) {
            LogHelper.error("One or more input files do not exist for FUZ conversion: " + fileName);
            return;
        }

        String[] command = ConfigManager.getSetting().getPackGenerateLip().equals("true") ?
                new String[]{encoderPath, fuzPath.toString(), xwmPath.toString(), lipPath.toString()} :
                new String[]{encoderPath, fuzPath.toString(), xwmPath.toString(), "-nolip"};

        executeProcess(command, "Convert XWM and LIP to FUZ");
    }

    private static void executeProcess(String[] command, String operationDescription) {
        LogHelper.debug("Executing process: " + String.join(" ", command));
        long startTime = System.currentTimeMillis();
        try {
            Process process = new ProcessBuilder(command).start();
            int exitCode = process.waitFor();
            long endTime = System.currentTimeMillis();
            LogHelper.performance(operationDescription + " took " + (endTime - startTime) + "ms");
            if (exitCode != 0) {
                LogHelper.error(operationDescription + " failed with exit code: " + exitCode);
            } else {
                LogHelper.grayInfo(operationDescription + " completed successfully for: " + command[command.length - 1]);
            }
        } catch (IOException | InterruptedException e) {
            LogHelper.error(operationDescription + " encountered an error: " + e.getMessage());
        }
    }
}
