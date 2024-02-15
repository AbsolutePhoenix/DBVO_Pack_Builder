package com.absolutephoenix.dbvopackbuilder.utils;

import com.absolutephoenix.dbvopackbuilder.ui.MainWindow;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The LogHelper class provides logging functionality with different severity levels.
 * It formats log messages with a timestamp and displays them in the MainWindow with associated colors.
 */
@SuppressWarnings("unused")
public class LogHelper {
    // A SimpleDateFormat to format the timestamps in the logs.
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Enum defining logging levels with associated colors for display in the user interface.
     */
    public enum Level {
        FATAL(Color.MAGENTA),
        ERROR(Color.RED),
        WARN(Color.ORANGE),
        NOTICE(Color.ORANGE.darker()),
        INFO(Color.CYAN.darker().darker()),
        REDINFO(Color.RED.darker()),  // Special case of INFO where the color is red.
        GREENINFO(Color.green.darker()),  // Special case of INFO where the color is red.
        GRAYINFO(Color.lightGray.darker().darker()),  // Special case of INFO where the color is red.
        YELLOWINFO(Color.yellow.darker()),  // Special case of INFO where the color is red.

        PERFORMANCE(Color.cyan.darker().darker().darker()),
        DEBUG(Color.GREEN.darker().darker().darker()),
        VERBOSE(Color.LIGHT_GRAY),
        TRACE(Color.GRAY);

        private final Color color;

        Level(Color color) {
            this.color = color;
        }

        /**
         * Retrieves the color associated with the logging level.
         * @return The color for the log level.
         */
        public Color getColor() {
            return color;
        }

        public String getDisplayName() {
            return this == REDINFO || this == GREENINFO || this == GRAYINFO || this == YELLOWINFO ? "INFO" : this.name();
        }
    }

    private static PrintWriter logFileWriter;

    private static final boolean reducedLogging = true;

    /**
     * Logs a message with a specified logging level.
     * @param level   The level of the log message.
     * @param message The message to be logged.
     */
    private static synchronized void log(Level level, String message) {

        long currentTimeMillis = System.currentTimeMillis();
        String timestamp = dateFormat.format(new Date(currentTimeMillis));  // Efficient timestamp creation
        String levelName = level.getDisplayName();  // Optimized level name determination
        String formattedMessage = String.format("[%s] [%s] %s%n", timestamp, levelName, message);

        if (reducedLogging && (level == Level.YELLOWINFO || level == Level.GRAYINFO || level == Level.GREENINFO ||
                level == Level.REDINFO || level == Level.INFO || level == Level.NOTICE ||
                level == Level.WARN || level == Level.ERROR || level == Level.FATAL)) {
            MainWindow.instance.appendToConsole(formattedMessage, level.getColor());
        }

        if (logFileWriter != null) {
            logFileWriter.printf(formattedMessage);
        } else {
            initializeLogFileIfNeeded();  // Separate method to handle log file initialization
        }
    }

    private static void initializeLogFileIfNeeded() {
        if (logFileWriter == null) {
            File logDir = new File("log");
            if (!logDir.exists() && !logDir.mkdirs()) {
                System.err.println("Failed to create log directory");
                return;
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
            initializeLogFile("log/" + dateFormat.format(new Date()) + ".log");
        }
    }

    public static void initializeLogFile(String filePath) {
        try {
            logFileWriter = new PrintWriter(new FileWriter(filePath, true), true);
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }


    // Convenience methods for logging at specific levels. Each method calls the private log method with the level and message.

    public static void fatal(String message) {
        log(Level.FATAL, message);
    }

    public static void error(String message) {
        log(Level.ERROR, message);
    }

    public static void warn(String message) {
        log(Level.WARN, message);
    }

    public static void notice(String message) {
        log(Level.NOTICE, message);
    }

    public static void info(String message) {
        log(Level.INFO, message);
    }

    public static void redInfo(String message) {
        log(Level.REDINFO, message);
    }
    public static void greenInfo(String message) {
        log(Level.GREENINFO, message);
    }

    public static void grayInfo(String message) {
        log(Level.GRAYINFO, message);
    }
    public static void yellowInfo(String message) {
        log(Level.YELLOWINFO, message);
    }

    public static void performance(String message) {
        log(Level.PERFORMANCE, message);
    }

    public static void debug(String message) {
        log(Level.DEBUG, message);
    }

    public static void verbose(String message) {
        log(Level.VERBOSE, message);
    }

    public static void trace(String message) {
        log(Level.TRACE, message);
    }
}
