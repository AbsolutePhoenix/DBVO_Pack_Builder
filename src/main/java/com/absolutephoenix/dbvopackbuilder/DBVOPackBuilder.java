package com.absolutephoenix.dbvopackbuilder;

import com.absolutephoenix.dbvopackbuilder.ui.MainWindow;
import com.absolutephoenix.dbvopackbuilder.ui.SplashScreen;
import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.OneDarkTheme;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

public class DBVOPackBuilder {
    static int splashTime = 5;

    public static void main(String[] args) {
        LafManager.setTheme(new OneDarkTheme());
        LafManager.install();
        if(!new File("topics").exists())
            new File("topics").mkdirs();
        new SplashScreen(splashTime).setVisible(true);
    }
}