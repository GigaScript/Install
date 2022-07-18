package com.company;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Logger {
    private static StringBuilder log = new StringBuilder();
    private final File logFilePath;

    public Logger(String logFilePath) {
        this.logFilePath = new File(logFilePath);
    }

    public void appendLogMessage(String message) {
        log.append(new Date() + " " + message);
    }

    public String getLog() {
        return log.toString();
    }

    public void saveLogFile() {
        if (logFilePath.exists()) {
            try (BufferedWriter fileToSave = new BufferedWriter(new FileWriter(logFilePath, true))) {
                fileToSave.append(log.toString());
                log = new StringBuilder();
            } catch (IOException ex) {
                appendLogMessage("Попытка сохранить ЛОГ(" + logFilePath + ") завершена с ошибкой: " + ex.getMessage());
            }
        }
    }
}
