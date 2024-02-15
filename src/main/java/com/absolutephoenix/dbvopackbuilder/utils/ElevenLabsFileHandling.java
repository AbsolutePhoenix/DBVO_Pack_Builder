package com.absolutephoenix.dbvopackbuilder.utils;

import com.absolutephoenix.dbvopackbuilder.config.ConfigManager;
import com.absolutephoenix.dbvopackbuilder.reference.GlobalVariables;
import com.absolutephoenix.dbvopackbuilder.ui.MainWindow;
import com.absolutephoenix.dbvopackbuilder.ui.panels.PackGeneratorPanel;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import net.andrewcpu.elevenlabs.model.voice.Voice;
import net.andrewcpu.elevenlabs.model.voice.VoiceSettings;

import java.io.*;

public class ElevenLabsFileHandling {
    private static final String MP3_DIR_PATH = "./staging/mp3/";

    public static boolean saveStreamAsMp3(Voice voice, double stability, double similarity, double style, String voiceLine, String fileName) {
        LogHelper.debug("Entering saveStreamAsMp3 for: " + voiceLine);
        File mp3File = new File(MP3_DIR_PATH, fileName + ".mp3");

        if (mp3File.exists()) {
            LogHelper.yellowInfo("SKIPPING: " + voiceLine + " (file already exists)");
            return true;
        }

        if (!ensureDirectoryExists()) {
            LogHelper.error("Could not create staging directory for: " + MP3_DIR_PATH);
            return false;
        }

        LogHelper.greenInfo("GENERATING: " + voiceLine);
        try{
            InputStream stream;
            try {
                stream = voice.generateStream(voiceLine, ConfigManager.getSetting().getElevenLabsVoiceModelID(), new VoiceSettings(stability, similarity, style, false));
            }catch (RuntimeException a){
                LogHelper.error("An issue has occured while taking to the ElevenLabs API. Check your settings and try again.");
                LogHelper.error("(Note. English V2 Model can not be used to generate voices. Use Multilingual V2)");
                return false;
            }
            OutputStream outputStream = new FileOutputStream(mp3File);

            updateCharacterCount(voiceLine);

            byte[] buffer = new byte[4096];
            int bytesRead;
            int totalBytesRead = 0;
            while ((bytesRead = stream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                LogHelper.debug("Writing to MP3: " + totalBytesRead + " bytes written");
            }
            LogHelper.debug("MP3 saved: " + fileName);
        } catch (InvalidDefinitionException e){
            //LogHelper.error(e.getMessage());
            return false;
        } catch (IOException e) {
            LogHelper.error("Failed to save MP3 file for " + fileName + ": " + e.getMessage());
            return false;
        }
        LogHelper.debug("Exiting saveStreamAsMp3 for: " + voiceLine);
        return true;
    }

    private static boolean ensureDirectoryExists() {
        File dir = new File(ElevenLabsFileHandling.MP3_DIR_PATH);
        return dir.exists() || dir.mkdirs();
    }

    private static void updateCharacterCount(String text) {
        GlobalVariables.CharacterCount += text.length();
        try {
            MainWindow.instance.setTitle("DBVO Pack Builder - " + GlobalVariables.CharacterCount + "/" + GlobalVariables.subscription.getCharacterLimit());
        } catch (NullPointerException ignore) {
        }
    }
}
