package com.absolutephoenix.dbvopackbuilder.ui;

import com.absolutephoenix.dbvopackbuilder.config.ConfigManager;
import com.absolutephoenix.dbvopackbuilder.reference.GlobalVariables;
import com.absolutephoenix.dbvopackbuilder.ui.panels.PackGeneratorPanel;
import com.absolutephoenix.dbvopackbuilder.ui.panels.SettingsPanel;
import com.absolutephoenix.dbvopackbuilder.utils.LogHelper;
import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.*;
import com.sun.tools.javac.Main;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.invoke.SwitchPoint;
import java.util.Objects;

public class MainWindow extends JFrame implements ComponentListener {
    private JTextPane consolePane = new JTextPane();
    private StyledDocument consoleDoc;

    public static MainWindow instance = null;
    JScrollPane scrollPane = new JScrollPane(consolePane);
    public JTabbedPane mainPane = new JTabbedPane();
    public SettingsPanel settingsPanel = new SettingsPanel();
    public PackGeneratorPanel packGeneratorPanel = new PackGeneratorPanel();

    public SplashScreen splashScreen;

    public JMenuBar mainBar = new JMenuBar();
    public JMenu themeMenu = new JMenu("Themes");

    public JMenuItem IntelliJTheme = new JMenuItem("IntelliJ  ");
    public JMenuItem SolarizedLightTheme = new JMenuItem("Solarized Light  ");
    public JMenuItem HighContrastLightTheme = new JMenuItem("High Contrast Light  ");
    public JMenuItem OneDarkTheme = new JMenuItem("One Dark  ");
    public JMenuItem DarculaTheme = new JMenuItem("Darcula  ");
    public JMenuItem SolarizedDarkTheme = new JMenuItem("Solarized Dark  ");
    public JMenuItem HighContrastDarkTheme = new JMenuItem("High Contrast Dark  ");

    public MainWindow(){
        showSplashScreen();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("new");
        instance = this;
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(1280, 720));
        this.setResizable(false);
        this.addComponentListener(this);
        this.setTitle("DBVO Pack Builder");
        this.setLayout(null);
        addElements();
        this.pack();
        this.setLocationRelativeTo(null);
        SetTheme();
        this.setVisible(true);
        closeSplashScreen();
        try {
            GlobalVariables.CharacterCount = GlobalVariables.subscription.getCharacterCount();
            MainWindow.instance.setTitle("DBVO Pack Builder - " + GlobalVariables.CharacterCount+ "/" + GlobalVariables.subscription.getCharacterLimit());
        }catch (NullPointerException ignore){
        }
        LogHelper.notice("DBVO Pack Maker Version: 0.0.1");
        LogHelper.notice("Make sure your settings are right before continuing,");
        LogHelper.notice("Character counts are an approximation before parsing. Actual count will most likely be lower.");
        packGeneratorPanel.loadTableData();

    }
    private void addElements(){
        add(mainBar);
        mainBar.add(themeMenu);
        themeMenu.add(IntelliJTheme);
        themeMenu.add(SolarizedLightTheme);
        themeMenu.add(HighContrastLightTheme);
        themeMenu.addSeparator();
        themeMenu.add(DarculaTheme);
        themeMenu.add(SolarizedDarkTheme);
        themeMenu.add(HighContrastDarkTheme);
        themeMenu.add(OneDarkTheme);
        IntelliJTheme.addActionListener(e -> {ConfigManager.getSetting().setCurrentTheme("IntelliJTheme"); SetTheme();});
        SolarizedLightTheme.addActionListener(e -> {ConfigManager.getSetting().setCurrentTheme("SolarizedLightTheme"); SetTheme();});
        HighContrastLightTheme.addActionListener(e -> {ConfigManager.getSetting().setCurrentTheme("HighContrastLightTheme"); SetTheme();});
        DarculaTheme.addActionListener(e -> {ConfigManager.getSetting().setCurrentTheme("DarculaTheme"); SetTheme();});
        SolarizedDarkTheme.addActionListener(e -> {ConfigManager.getSetting().setCurrentTheme("SolarizedDarkTheme"); SetTheme();});
        HighContrastDarkTheme.addActionListener(e -> {ConfigManager.getSetting().setCurrentTheme("HighContrastDarkTheme"); SetTheme();});
        OneDarkTheme.addActionListener(e -> {ConfigManager.getSetting().setCurrentTheme("OneDarkTheme"); SetTheme();});

        //redirectSystemStreams();
        mainPane.addTab("Settings", settingsPanel);
        mainPane.addTab("Pack Generator", packGeneratorPanel);
        add(mainPane);

        mainPane.setBorder(BorderFactory.createBevelBorder(1));
        scrollPane.setBorder(BorderFactory.createBevelBorder(1));
        consolePane.setEditable(false);
        consoleDoc = consolePane.getStyledDocument();
        add(scrollPane);
    }
    private void redirectSystemStreams() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                appendToConsole(String.valueOf((char) b), Color.BLACK);
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                appendToConsole(new String(b, off, len), Color.WHITE);
            }

            @Override
            public void write(byte[] b) throws IOException {
                write(b, 0, b.length);
            }
        };

        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }
    public void appendToConsole(String text, Color color) {
        Style style = consolePane.addStyle("Style", null);
        StyleConstants.setForeground(style, color);
        SwingUtilities.invokeLater(() -> {
            try {
                consoleDoc.insertString(consoleDoc.getLength(), text, style);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });
    }

    private void SetTheme() {
        String currentTheme = ConfigManager.getSetting().getCurrentTheme();
        resetAllIcons();
        setThemeIcon(currentTheme);
        applyTheme(currentTheme);
        ConfigManager.getSetting().setCurrentTheme(currentTheme);
        ConfigManager.getSetting().saveProperties(false);
    }

    private void resetAllIcons() {
        IntelliJTheme.setIcon(null);
        SolarizedLightTheme.setIcon(null);
        HighContrastLightTheme.setIcon(null);
        DarculaTheme.setIcon(null);
        SolarizedDarkTheme.setIcon(null);
        HighContrastDarkTheme.setIcon(null);
        OneDarkTheme.setIcon(null);
    }

    private void setThemeIcon(String theme) {
        JMenuItem selectedThemeItem = getThemeMenuItem(theme);
        if (selectedThemeItem != null) {
            selectedThemeItem.setIcon(resizeIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/img/check.png"))), 12));
        }
    }

    private JMenuItem getThemeMenuItem(String theme) {
        switch (theme) {
            case "IntelliJTheme":
                return IntelliJTheme;
            case "SolarizedLightTheme":
                return SolarizedLightTheme;
            case "HighContrastLightTheme":
                return HighContrastLightTheme;
            case "DarculaTheme":
                return DarculaTheme;
            case "SolarizedDarkTheme":
                return SolarizedDarkTheme;
            case "HighContrastDarkTheme":
                return HighContrastDarkTheme;
            case "OneDarkTheme":
                return OneDarkTheme;
            default:
                return null;
        }
    }

    private void applyTheme(String theme) {
        switch (theme) {
            case "IntelliJTheme":
                LafManager.setTheme(new IntelliJTheme());
                break;
            case "SolarizedLightTheme":
                LafManager.setTheme(new SolarizedLightTheme());
                break;
            case "HighContrastLightTheme":
                LafManager.setTheme(new HighContrastLightTheme());
                break;
            case "DarculaTheme":
                LafManager.setTheme(new DarculaTheme());
                break;
            case "SolarizedDarkTheme":
                LafManager.setTheme(new SolarizedDarkTheme());
                break;
            case "HighContrastDarkTheme":
                LafManager.setTheme(new HighContrastDarkTheme());
                break;
            case "OneDarkTheme":
                LafManager.setTheme(new OneDarkTheme());
                break;
        }
        LafManager.install();
    }

    private void showSplashScreen() {
        splashScreen = new SplashScreen(10); // Duration is just a placeholder
        splashScreen.setVisible(true);
    }

    private void closeSplashScreen() {
        if (splashScreen != null) {
            splashScreen.close();
        }
    }

    public static ImageIcon resizeIcon(ImageIcon icon, int maxHeight) {
        int width = icon.getIconWidth();
        int height = icon.getIconHeight();

        // Calculate the new width to maintain the aspect ratio
        double aspectRatio = (double) width / height;
        int newWidth = (int) (maxHeight * aspectRatio);

        // Resize the image
        Image resizedImage = icon.getImage().getScaledInstance(newWidth, maxHeight, Image.SCALE_SMOOTH);

        return new ImageIcon(resizedImage);
    }
    @Override
    public void componentResized(ComponentEvent e) {
        mainBar.setBounds(0, 0, this.getContentPane().getWidth(), 20);
        scrollPane.setBounds(10, this.getContentPane().getHeight() - 240, this.getContentPane().getWidth() - 20, 225);
        mainPane.setBounds(10, 20, this.getContentPane().getWidth() - 20, scrollPane.getY() - 20);

    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}
