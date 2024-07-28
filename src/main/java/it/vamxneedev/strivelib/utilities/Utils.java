package it.vamxneedev.strivelib.utilities;

import it.vamxneedev.strivelib.utilities.colors.IridiumColorAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Utils {
    public static String getPluginName(Plugin instance) { return instance.getDescription().getName(); }
    public static String getPluginVersion(Plugin instance) { return instance.getDescription().getVersion(); }

    public static String colorize(String msg) { return IridiumColorAPI.process(msg); }
    public static List<String> colorizeList (List<String> listOfMsgs) {
        List<String> listFormatted = new ArrayList<>();
        for (String msg : listOfMsgs) {
            listFormatted.add(colorize(msg));
        }
        return listFormatted;
    }

    public static void sendConsoleMsg(String msg) {
        ConsoleCommandSender sender = Bukkit.getConsoleSender();
        sender.sendMessage(colorize(msg));
    }

    public static String getCurrentDate(String format) {
        DateFormat formatter = new SimpleDateFormat(format);
        Date date = new Date();
        return formatter.format(date);
    }
    public static String getCurrentDate() {
        DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        return formatter.format(date);
    }

    public static String getCurrentTime(String format) {
        DateFormat formatter = new SimpleDateFormat(format);
        Date time = new Date();
        return formatter.format(time);
    }
    public static String getCurrentTime() {
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date time = new Date();
        return formatter.format(time);
    }
    public static String getTimeFromMilliSecs(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        return String.format("%02d:%02d:%02d:%02d", days, hours % 24, minutes % 60, seconds % 60);
    }

    public static String formatNumber(int number) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        return decimalFormat.format(number);
    }
    public static String formatNumber(double number) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
        return decimalFormat.format(number);
    }
    public static int getRandomNumber(int min, int max) {
        return (int) (Math.random() * (max - min + 1) + min);
    }
    public static double getNextRandomDouble() {
        Random random = new Random();
        return random.nextDouble();
    }

    public static int ticksToSeconds(int ticks) {
        return ticks / 20;
    }
    public static int secondsToTicks(int seconds) {
        return seconds * 20;
    }

    public static void executeConsoleCmd(String command) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }
    public static void executePlayerCmd(Player player, String command) {
        player.performCommand(command);
    }

    public static int versionToNumber() {
        String version = Bukkit.getVersion();
        if (version.contains("1.8"))
            return 18;
        if (version.contains("1.9"))
            return 19;
        if (version.contains("1.10"))
            return 110;
        if (version.contains("1.11"))
            return 111;
        if (version.contains("1.12"))
            return 112;
        if (version.contains("1.13"))
            return 113;
        if (version.contains("1.14"))
            return 114;
        if (version.contains("1.15"))
            return 115;
        if (version.contains("1.16"))
            return 116;
        if (version.contains("1.17"))
            return 117;
        if (version.contains("1.18"))
            return 118;
        if (version.contains("1.19"))
            return 119;
        if (version.contains("1.20"))
            return 120;
        if(version.contains("1.21"))
            return 121;
        return 500;
    }

    public static void checkPath(String x) {
        Path filePath = Paths.get(x);
        Path parentPath = filePath.getParent();
        if(parentPath != null) {
            try {
                Files.createDirectories(parentPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if(!Files.exists(filePath)) {
            try {
                Files.createFile(filePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static double calcElo(double eloDifference, int k, double score) {
        double eloDifferenceRatio = eloDifference / 400;
        double exponentialValue = Math.pow(10, eloDifferenceRatio) + 1;
        double expected = 1 / exponentialValue;

        return (k * (score - expected));
    }
}
