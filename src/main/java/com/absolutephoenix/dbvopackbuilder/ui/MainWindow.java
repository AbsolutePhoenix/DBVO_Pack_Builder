package com.absolutephoenix.dbvopackbuilder.ui;

import com.absolutephoenix.dbvopackbuilder.reference.GlobalVariables;
import com.absolutephoenix.dbvopackbuilder.ui.panels.PackGeneratorPanel;
import com.absolutephoenix.dbvopackbuilder.ui.panels.SettingsPanel;
import com.absolutephoenix.dbvopackbuilder.utils.LogHelper;
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

public class MainWindow extends JFrame implements ComponentListener {
    private JTextPane consolePane = new JTextPane();
    private StyledDocument consoleDoc;

    public static MainWindow instance = null;
    JScrollPane scrollPane = new JScrollPane(consolePane);
    public JTabbedPane mainPane = new JTabbedPane();
    public SettingsPanel settingsPanel = new SettingsPanel();
    public PackGeneratorPanel packGeneratorPanel = new PackGeneratorPanel();
    public MainWindow(){
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
        this.setVisible(true);
        try {
            GlobalVariables.CharacterCount = GlobalVariables.subscription.getCharacterCount();
            MainWindow.instance.setTitle("DBVO Pack Builder - " + GlobalVariables.CharacterCount+ "/" + GlobalVariables.subscription.getCharacterLimit());
        }catch (NullPointerException ignore){
        }
    }
    private void addElements(){
        //redirectSystemStreams();
        mainPane.addTab("Settings", settingsPanel);
        mainPane.addTab("Pack Generator", packGeneratorPanel);
        this.add(mainPane);

        mainPane.setBorder(BorderFactory.createBevelBorder(1));
        scrollPane.setBorder(BorderFactory.createBevelBorder(1));
        consolePane.setEditable(false);
        consoleDoc = consolePane.getStyledDocument();
        this.add(scrollPane);
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

    public boolean hasTriggeredStartup = false;
    @Override
    public void componentResized(ComponentEvent e) {
        scrollPane.setBounds(10, this.getContentPane().getHeight() - 240, this.getContentPane().getWidth() - 20, 225);
        mainPane.setBounds(10, 10, this.getContentPane().getWidth() - 20, scrollPane.getY() - 20);
        if(!hasTriggeredStartup){
            LogHelper.notice("DBVO Pack Maker Version: 0.0.1");
            LogHelper.notice("Make sure your settings are right before continuing,");
            LogHelper.notice("Character counts are an approximation before parsing. Actual count will most likely be lower.");
            hasTriggeredStartup = true;
        }
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
