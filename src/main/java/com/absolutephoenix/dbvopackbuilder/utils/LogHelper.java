package com.absolutephoenix.dbvopackbuilder.utils;

import com.absolutephoenix.dbvopackbuilder.ui.MainWindow;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The LogHelper class provides logging functionality with different severity levels.
 * It formats log messages with a timestamp and displays them in the MainWindow with associated colors.
 */
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
        INFO(Color.CYAN),
        REDINFO(Color.RED),  // Special case of INFO where the color is red.
        GREENINFO(Color.green),  // Special case of INFO where the color is red.
        GRAYINFO(Color.lightGray),  // Special case of INFO where the color is red.
        YELLOWINFO(Color.yellow.darker()),  // Special case of INFO where the color is red.

        PERFORMANCE(Color.cyan.darker()),
        DEBUG(Color.GREEN),
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
    }

    /**
     * Logs a message with a specified logging level.
     * @param level   The level of the log message.
     * @param message The message to be logged.
     */
    private static void log(Level level, String message) {
        String timestamp = dateFormat.format(new Date());  // Get the current time for the timestamp
        String levelName = (level == Level.REDINFO || level == Level.GREENINFO || level == Level.GRAYINFO || level == Level.YELLOWINFO) ? "INFO" : level.name();
        String formattedMessage = String.format("[%s] [%s] %s%n", timestamp, levelName, message);  // Format the log message

        // Append the formatted message to the MainWindow's console with the appropriate color.
        MainWindow.instance.appendToConsole(formattedMessage, level.getColor());
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
