package com.absolutephoenix.dbvopackbuilder.ui;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * A simple splash screen implementation using a JWindow.
 */
public class SplashScreen extends JWindow {

    /**
     * Constructor for the splash screen.
     *
     * @param duration The duration in seconds for which the splash screen is visible.
     */
    public SplashScreen(int duration) {
        // Get the content pane and set its background color to white
        JPanel content = (JPanel)getContentPane();
        content.setBackground(Color.white);

        // Set the window's bounds, centering the window on the screen
        int width = 600;
        int height = 300;
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        setBounds(x, y, width, height);

        // Create the components for the splash screen
        JLabel label = new JLabel(new ImageIcon(Objects.requireNonNull(SplashScreen.class.getResource("/img/splash.png"))));
        JLabel copyrt = new JLabel("Your application is loading...", JLabel.CENTER);
        copyrt.setFont(new Font("Sans-Serif", Font.BOLD, 12));
        content.add(label, BorderLayout.CENTER);
        content.add(copyrt, BorderLayout.SOUTH);
        Color blackBorder = new Color(0, 0, 0, 255); // Black color with full opacity
        content.setBorder(BorderFactory.createLineBorder(blackBorder, 5));

        // Make the splash screen visible
        setVisible(true);

        // Create a timer to hide the splash screen after a certain duration
//        Timer timer = new Timer(duration * 1000, e -> {
//            dispose(); // Dispose of the splash screen window
//            // After the splash screen is disposed, make the main application window visible
//                new MainWindow();
//        });
//        timer.setRepeats(false); // Ensure the timer only runs once
//        timer.start(); // Start the timer
    }

    public void close(){
        dispose();
    }
}