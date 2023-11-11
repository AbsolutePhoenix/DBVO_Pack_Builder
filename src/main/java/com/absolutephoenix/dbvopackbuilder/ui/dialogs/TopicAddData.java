package com.absolutephoenix.dbvopackbuilder.ui.dialogs;

import java.io.File;

class TopicAddData {
    private String modName;
    private String espName;
    private File filePath;

    public TopicAddData(String modName, String espName, File filePath) {
        this.modName = modName;
        this.espName = espName;
        this.filePath = filePath;
    }

    public String getModName() {
        return modName;
    }

    public String getEspName() {
        return espName;
    }

    public File getFilePath() {
        return filePath;
    }
}