package com.absolutephoenix.dbvopackbuilder.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Singleton class that manages application configuration settings by loading from and saving to a properties file.
 */
@SuppressWarnings({"CallToPrintStackTrace", "ResultOfMethodCallIgnored"})
public class ConfigManager {
    private static ConfigManager ProgramSettings;
    private final Properties properties;
    private final String configFilePath;
    private final static String configFolder = "config";
    private final static String configFileName = "settings.properties";

    /**
     * Private constructor for singleton pattern.
     * Initializes the configuration file path and loads the properties from the file.
     */
    private ConfigManager() {
        this.configFilePath = configFolder + File.separator + configFileName;
        this.properties = new Properties();
        ensureConfigFolderExists();
        loadProperties();
    }

    /**
     * Ensures that the configuration folder exists.
     * If it does not exist, it creates the directory structure.
     */
    private void ensureConfigFolderExists() {
        File folder = new File(configFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    /**
     * Returns the singleton instance of the ConfigManager.
     * If the instance does not exist, it initializes a new instance.
     *
     * @return The single instance of ConfigManager.
     */
    public static ConfigManager getSetting() {
        if (ProgramSettings == null) {
            ProgramSettings = new ConfigManager();
        }
        return ProgramSettings;
    }

    /**
     * Loads the properties from the configuration file.
     * If the file does not exist or an error occurs, it saves the properties.
     */
    private void loadProperties() {
        try (FileInputStream input = new FileInputStream(configFilePath)) {
            properties.load(input);
        } catch (IOException ex) {
            saveProperties(true);
        }
    }

    /**
     * Saves the current properties to the configuration file.
     * If rerunLoad is true, it will reload the properties after saving.
     *
     * @param rerunLoad If true, properties will be reloaded after saving.
     */
    public void saveProperties(boolean rerunLoad) {
        try (FileOutputStream output = new FileOutputStream(configFilePath)) {
            properties.store(output, "Application Configuration");
            if (rerunLoad) {
                loadProperties();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // The following methods are getter and setter pairs for various configuration properties.
    // Each property is identified by a unique key, and provides a default value when the property is not found.

    // For each property, the getter method retrieves the value from the properties object, providing a default value if the key is not found.
    // The setter method updates the properties object with the new value for the corresponding key.

    public String getElevenLabsAPIKey() {
        return properties.getProperty("ElevenLabsAPIKey", "");
    }

    public void setElevenLabsAPIKey(String apiKey) {
        properties.setProperty("ElevenLabsAPIKey", apiKey);
    }

    public String getElevenLabsVoice() {
        return properties.getProperty("ElevenLabsVoice", "");
    }

    public void setElevenLabsVoice(String voiceName) {
        properties.setProperty("ElevenLabsVoice", voiceName);
    }

    public String getElevenLabsVoiceModel() {
        return properties.getProperty("ElevenLabsVoiceModel", "");
    }

    public void setElevenLabsVoiceModel(String modelName) {
        properties.setProperty("ElevenLabsVoiceModel", modelName);
    }

    public String getElevenLabsVoiceID() {
        return properties.getProperty("ElevenLabsVoiceID", "");
    }

    public void setElevenLabsVoiceID(String voiceID) {
        properties.setProperty("ElevenLabsVoiceID", voiceID);
    }

    public String getElevenLabsVoiceModelID() {
        return properties.getProperty("ElevenLabsVoiceModelID", "");
    }

    public void setElevenLabsVoiceModelID(String voiceModelID) {
        properties.setProperty("ElevenLabsVoiceModelID", voiceModelID);
    }

    public int getElevenLabsStability() {
        return Integer.parseInt(properties.getProperty("ElevenStability", "50"));
    }

    public void setElevenLabsStability(int stability) {
        properties.setProperty("ElevenStability", stability + "");
    }

    public int getElevenLabsClarity() {
        return Integer.parseInt(properties.getProperty("ElevenLabsClarity", "50"));
    }

    public void setElevenLabsClarity(int clarity) {
        properties.setProperty("ElevenLabsClarity", clarity + "");
    }

    public int getElevenLabsStyle() {
        return Integer.parseInt(properties.getProperty("ElevenLabsStyle", "0"));
    }

    public void setElevenLabsStyle(int style) {
        properties.setProperty("ElevenLabsStyle", style + "");
    }


    public String getWriteAliasData() {
        return properties.getProperty("SaveAlias", "false");
    }

    public void setWriteAliasData(String fomodNexusURL) {
        properties.setProperty("SaveAlias", fomodNexusURL);
    }

    public String getWriteCharacterData() {
        return properties.getProperty("SaveCharacter", "false");
    }

    public void setWriteCharacterData(String fomodNexusURL) {
        properties.setProperty("SaveCharacter", fomodNexusURL);
    }

    public String getWriteChildData() {
        return properties.getProperty("SaveChild", "false");
    }

    public void setWriteChildData(String fomodNexusURL) {
        properties.setProperty("SaveChild", fomodNexusURL);
    }

    public String getPackName() {
        return properties.getProperty("PackName", "");
    }

    public void setPackName(String packName) {
        properties.setProperty("PackName", packName);
    }

    public String getPackID() {
        return properties.getProperty("PackID", "");
    }

    public void setPackID(String packID) {
        properties.setProperty("PackID", packID);
    }

    public String getPackVoiceName() {
        return properties.getProperty("PackVoiceName", "");
    }

    public void setPackVoiceName(String packVoiceName) {
        properties.setProperty("PackVoiceName", packVoiceName);
    }

    public String getPackGenerateLip() {
        return properties.getProperty("PackGenerateLip", "true");
    }

    public void setPackGenerateLip(String packGenerateLip) {
        properties.setProperty("PackGenerateLip", packGenerateLip);
    }

    public String getPackBuildToBSA() {
        return properties.getProperty("PackBuildToBSA", "false");
    }

    public void setPackBuildToBSA(String packBuildToBSA) {
        properties.setProperty("PackBuildToBSA", packBuildToBSA);
    }


    public String getFomodModName() {
        return properties.getProperty("FomodModName", "");
    }

    public void setFomodModName(String fomodModName) {
        properties.setProperty("FomodModName", fomodModName);
    }

    public String getFomodAuthorName() {
        return properties.getProperty("FomodAuthorName", "");
    }

    public void setFomodAuthorName(String fomodAuthorName) {
        properties.setProperty("FomodAuthorName", fomodAuthorName);
    }

    public String getFomodVersion() {
        return properties.getProperty("FomodVersion", "");
    }

    public void setFomodVersion(String fomodVersion) {
        properties.setProperty("FomodVersion", fomodVersion);
    }

    public String getFomodNexusURL() {
        return properties.getProperty("FomodNexusURL", "");
    }

    public void setFomodNexusURL(String fomodNexusURL) {
        properties.setProperty("FomodNexusURL", fomodNexusURL);
    }

    public String getCurrentTheme() {
        return properties.getProperty("CurrentTheme", "OneDarkTheme");
    }
    public void setCurrentTheme(String currentTheme) {
        properties.setProperty("CurrentTheme", currentTheme);
    }

}