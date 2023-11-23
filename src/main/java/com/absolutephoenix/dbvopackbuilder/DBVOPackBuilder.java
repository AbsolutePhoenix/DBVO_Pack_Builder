package com.absolutephoenix.dbvopackbuilder;

import com.absolutephoenix.dbvopackbuilder.ui.MainWindow;
import com.absolutephoenix.dbvopackbuilder.ui.SplashScreen;
import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.*;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

public class DBVOPackBuilder {

    public static void main(String[] args) {
        LafManager.setTheme(new OneDarkTheme());
        LafManager.install();
        if(!new File("topics").exists())
            new File("topics").mkdirs();
        new MainWindow();
    }
}