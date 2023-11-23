package com.absolutephoenix.dbvopackbuilder.utils;

import com.absolutephoenix.dbvopackbuilder.config.ConfigManager;
import com.absolutephoenix.dbvopackbuilder.reference.GlobalVariables;
import com.absolutephoenix.dbvopackbuilder.ui.MainWindow;
import net.andrewcpu.elevenlabs.ElevenLabs;
import net.andrewcpu.elevenlabs.model.response.GenerationTypeModel;
import net.andrewcpu.elevenlabs.model.voice.Voice;
import net.andrewcpu.elevenlabs.model.voice.VoiceSettings;

import java.io.*;

public class ElevenLabsFileHandling {
    public static void saveStreamAsMp3(Voice voice, double stability, double similarity, double style, String voiceLine,  String fileName) {
        File mp3Dir = new File("./staging/mp3/");

        // Ensure the temp directory exists
        if (!mp3Dir.exists() && !mp3Dir.mkdirs()) {
            System.out.println("Could not create staging directory.");
            return;
        }

        if(new File(mp3Dir, fileName + ".mp3").exists()){
            LogHelper.yellowInfo("SKIPPING: " + voiceLine);
        }else {
            LogHelper.greenInfo("GENERATING: " + voiceLine);

            InputStream stream = voice.generateStream( voiceLine, ConfigManager.getSetting().getElevenLabsVoiceModelID(), new VoiceSettings(stability, similarity, style, false));
            try {
                GlobalVariables.CharacterCount = GlobalVariables.CharacterCount + voiceLine.length();
                MainWindow.instance.setTitle("DBVO Pack Builder - " + GlobalVariables.CharacterCount + "/" + GlobalVariables.subscription.getCharacterLimit());
            }catch (NullPointerException ignore){
            }
            // Construct the full path for the MP3 file in the temp directory
            File mp3File = new File(mp3Dir, fileName + ".mp3");
            try (OutputStream outputStream = new FileOutputStream(mp3File)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                // Read bytes from the input stream and write them to the output file
                while ((bytesRead = stream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                System.out.println("MP3: " + fileName);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to save MP3 file.");
            } finally {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
