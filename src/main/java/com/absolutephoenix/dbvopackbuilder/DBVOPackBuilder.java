package com.absolutephoenix.dbvopackbuilder;

import com.absolutephoenix.dbvopackbuilder.ui.MainWindow;
import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.*;

import java.io.*;

public class DBVOPackBuilder {

    public static void main(String[] args) {
        LafManager.setTheme(new OneDarkTheme());
        LafManager.install();
        if(!new File("topics").exists())
            //noinspection ResultOfMethodCallIgnored
            new File("topics").mkdirs();
        new MainWindow();
    }
}