package com.company;

import java.io.*;
import java.util.*;

public class Main {
    public static String baseDir = "D://jtemp/Games";
    public static LinkedHashMap<String, LinkedList<String>> dirList = new LinkedHashMap<>();
    public static String logFilePath = baseDir + "/temp/temp.txt";
    public static File logFile = new File(logFilePath);

    public static void main(String[] args) {
        dirList.put(baseDir, new LinkedList<String>(Arrays.asList(
                "/src", "/res", "/savegames", "/temp"
        )));
        dirList.put(baseDir + "/src", new LinkedList<String>(Arrays.asList(
                "/main", "/test"
        )));
        dirList.put(baseDir + "/src/main", new LinkedList<String>(Arrays.asList(
                "/Main.java", "/Utils.java"
        )));
        dirList.put(baseDir + "/res", new LinkedList<String>(Arrays.asList(
                "/drawables", "/vectors", "/icons"
        )));
        dirList.put(baseDir + "/temp", new LinkedList<String>(Arrays.asList(
                "/temp.txt"
        )));
        runInstall();
    }
    public static StringBuilder log = new StringBuilder();

    /*
    Папка jtemp на диске D должна быть создана до запуска программы
     */
    private static void runInstall() {
        appendLogMessage(" =======СТАРТ ПРОГРАММЫ=======\n");
        dirList.forEach((path, childrenPath) -> {
            createFolder(path);
            if (!childrenPath.isEmpty()) {
                for (int folder = 0; folder < childrenPath.size(); folder++) {
                    createFolder(path + childrenPath.get(folder));
                }
            }
        });
        appendLogMessage(" =======ПРОГРАММА ЗАВЕРШЕНА=======\n");
        saveLogFile();
    }

    public static void createFolder(String folderPath) {
        File directoryName = new File(folderPath);
        if (folderPath.contains(".")) {
            createFile(folderPath);
            return;
        }
        if (directoryName.exists()) {
            appendLogMessage(" Попытка создать каталог(" + folderPath + "): Каталог уже существует\n");
        } else {
            appendLogMessage(" Попытка создать каталог(" + folderPath + "): Каталог создан\n");
            directoryName.mkdir();
        }
    }

    private static void createFile(String folderPath) {
        File filePath = new File(folderPath);
        try {
            if (filePath.createNewFile()) {
                appendLogMessage(" Попытка создать файл(" + folderPath + "): Файл создан\n");
            } else {
                appendLogMessage(" Попытка создать файл(" + folderPath + "): Файл уже существует\n");
            }
        } catch (IOException ex) {
            appendLogMessage("Попытка создать файл(" + folderPath + ") завершена с ошибкой: "+ex.getMessage());
        }
    }

    private static void appendLogMessage(String message) {
        log.append(new Date() + message);
    }

    private static void saveLogFile() {
        if (logFile.exists()) {
            try (BufferedWriter fileToSave = new BufferedWriter(new FileWriter(logFile, true))) {
                fileToSave.append(log.toString());
            } catch (IOException ex) {
                appendLogMessage("Попытка сохранить ЛОГ(" + logFile + ") завершена с ошибкой: "+ex.getMessage());
            }
        }
    }
}
