package com.absolutephoenix.dbvopackbuilder.handlers;

import com.absolutephoenix.dbvopackbuilder.config.ConfigManager;
import com.absolutephoenix.dbvopackbuilder.utils.LogHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@SuppressWarnings({"RegExpRedundantEscape", "ResultOfMethodCallIgnored", "StatementWithEmptyBody"})
public class TopicHandler {

    public static List<String> initialTopicProcessing(String filePath, String modName){
        LogHelper.info("Starting initial topic processing for file: " + filePath + " and mod: " + modName);

        List<String> baseData = new ArrayList<>();

        File aliasFileName = new File("topics/ungenerated/"  + modName + "_Alias" + ".txt");
        File characterFileName = new File("topics/ungenerated/" + modName + "_Character" + ".txt");
        File childFileName = new File("topics/ungenerated/" + modName + "_Child" + ".txt");

        List<String> goodData = new ArrayList<>();
        //noinspection unused
        List<String> longData = new ArrayList<>();
        List<String> aliasData = new ArrayList<>();
        List<String> aliasDataCharacter = new ArrayList<>();
        List<String> aliasDataChild = new ArrayList<>();

        try {
            Scanner scanner = new Scanner(new File(filePath));
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                LogHelper.debug("Reading line: " + line);
                baseData.add(line);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            LogHelper.error("File not found: " + filePath + ". " + e.getMessage());
            throw new RuntimeException(e);
        }
        boolean isTooLong = false;
        for(String data: baseData){
                if (data.length() > 200) {
                    LogHelper.warn("Data too long, skipping processing: " + data);
                    isTooLong = true;
                    break;
            }
        }
        if(isTooLong) return null;

        for(String data: baseData){
            LogHelper.debug("Processing data: " + data);
            if(!data.contains("<") && !data.contains(">")) {
                if (data.startsWith("(") && data.endsWith(")")) {}
                else if (data.startsWith("[") && data.endsWith("]")) {}
                else if (data.startsWith("--") && data.endsWith("--")) {}
                else if (data.startsWith("*") && data.endsWith("*")) {}
                else if (!data.contains(" ") && data.contains("_")) {}
                else if(data.equals("...") | data.equals(" ...") | data.equals("... ") | data.equals(" ... ")){}
                else if (canConvertToHex(data)) {}
                else if (data.startsWith("$")){}
                else { goodData.add(data); }
            }else if(data.contains("(") && data.contains(")")){
                if(!data.substring(0, data.indexOf("(")).contains("Alias") || !data.substring(0, data.indexOf("(")).contains("Global")) {
                    goodData.add(data);
                }
                else {
                    aliasData.add(data);
                }

            }else {
                if(data.contains("<Alias=Player>")){
                    aliasDataCharacter.add(data);
                }else if(data.contains("<Alias.ShortName=Child1>")){
                    aliasDataChild.add(data);
                }else if(data.contains("<Alias.ShortName=Child2>")){
                }else {
                    aliasData.add(data);
                }
            }
        }

        if(ConfigManager.getSetting().getWriteAliasData().equals("true")) if(!aliasData.isEmpty()) writeToFile(aliasFileName, aliasData);
        if(ConfigManager.getSetting().getWriteCharacterData().equals("ture")) if(!aliasDataCharacter.isEmpty())writeToFile(characterFileName, aliasDataCharacter);
        if(ConfigManager.getSetting().getWriteChildData().equals("ture")) if(!aliasDataChild.isEmpty())writeToFile(childFileName, aliasDataChild);
        LogHelper.info("Initial topic processing completed for mod: " + modName);
        return goodData;
    }
    private static boolean canConvertToHex(String input) {
        if (input.matches("^[0-9A-Fa-f]+$")) {
            try {
                String hex = stringToHex(input);
                return !hex.isEmpty();
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return false;
    }
    private static String stringToHex(String input) {
        StringBuilder hex = new StringBuilder();
        for (char character : input.toCharArray()) {
            hex.append(String.format("%02X", (int) character));
        }
        return hex.toString();
    }

    private static void writeToFile(File file, List<String> array) {
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            // Convert List<String> to a single String with line breaks
            String content = String.join(System.lineSeparator(), array);

            // Write to file
            Files.write(Paths.get(file.getAbsolutePath()), content.getBytes(), StandardOpenOption.CREATE);

            System.out.println("File Created at: " + file.getAbsolutePath());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static List<String> getVoiceGenStrings(List<String> goodFileList){
        String regex = "\\s*\\(.*?\\)\\s*|\\s*\\*.*?\\*\\s*|\\s*\\[.*?\\]\\s*";
        List<String> resultList = new ArrayList<>();
        for(String data: goodFileList) {
            String result = data.replaceAll(regex, " ").trim();
            result = result.replaceAll("\\s+", " ");
            resultList.add(result);
        }

        return resultList;
    }
    public static List<String> getFileNames(List<String> goodFileList) {
        List<String> resultList = new ArrayList<>();
        for (String data : goodFileList) {
            String result = data.replaceAll("[\\\\/:*?\"<>|]", "_");
            result = result.replaceAll(" ", "_");
            result = result.replaceAll("(_?\\([^)]*\\))+\\s*$", "");
            resultList.add(result);
        }
        return resultList;
    }

}
