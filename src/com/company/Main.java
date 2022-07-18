package com.company;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {
    /*
   Папка jtemp на диске D должна быть создана до запуска программы
    */
    public static String baseDir = "D://jtemp/Games";
    public static String saveGamePath = baseDir + "/savegames";
    public static LinkedHashMap<String, LinkedList<String>> dirList = new InstallFileStructure(baseDir).getDirList();
    public static Logger logger = new Logger(baseDir + "/temp/temp.txt");

    public static void main(String[] args) {
        runInstall();
        startGame();
    }

    private static void startGame() {
        saveGame(saveGamePath + "/saveGame1.dat", new GameProgress(100, 1, 1, 10));
        saveGame(saveGamePath + "/saveGame2.dat", new GameProgress(90, 2, 3, 50));
        saveGame(saveGamePath + "/saveGame3.dat", new GameProgress(80, 3, 4, 70));
        logger.saveLogFile();
        createZip(saveGamePath + "/save.zip");
        openZip(saveGamePath + "/save.zip");
        GameProgress gameProgress = openProgress(saveGamePath + "/saveGame2.dat");
        System.out.println(gameProgress.toString());
        logger.saveLogFile();
    }

    private static GameProgress openProgress(String gameProgressFile) {
        GameProgress gameProgress = null;
        try (FileInputStream fileWhitGameProgress = new FileInputStream(gameProgressFile);
             ObjectInputStream fileContent = new ObjectInputStream(fileWhitGameProgress)) {
            gameProgress = (GameProgress) fileContent.readObject();
            logger.appendLogMessage("Успешно загружен объект ("+gameProgress.toString()+") из файла(" + gameProgressFile + ")\n");

        } catch (Exception ex) {
            logger.appendLogMessage("Ошибка в чтения файла (" + gameProgressFile + "): " + ex.getMessage() + "\n");
        }
        logger.saveLogFile();
        return gameProgress;
    }

    private static void openZip(String pathToZip) {
        try (ZipInputStream fileToUnZip = new ZipInputStream(new FileInputStream(pathToZip))) {
            ZipEntry entry;
            String fileName;
            while ((entry = fileToUnZip.getNextEntry()) != null) {
                fileName = entry.getName();
                FileOutputStream pathToUnZip = new FileOutputStream(saveGamePath + "/" + fileName);
                for (int fileContent = fileToUnZip.read(); fileContent != -1; fileContent = fileToUnZip.read()) {
                    pathToUnZip.write(fileContent);
                }
                pathToUnZip.flush();
                fileToUnZip.closeEntry();
                pathToUnZip.close();
            }

        } catch (Exception ex) {
            logger.appendLogMessage("Попытка распаковать файл (" + pathToZip + "): Ошибка" + ex.getMessage());
        }
        logger.saveLogFile();
    }

    private static void deleteFile(String directory, String extension) {
        File directoryForDeleteFiles = new File(directory);
        if (directoryForDeleteFiles.exists()) {
            File[] listFiles = directoryForDeleteFiles.listFiles(new FileExtensionFilter(".dat"));
            if (listFiles != null) {
                for (File file : listFiles) {
                    if (file.delete()) {
                        logger.appendLogMessage(" Попытка удалить файл в (" + directory + "): файл удален \n");
                    } else {
                        logger.appendLogMessage(" Попытка удалить файл в (" + directory + "): файл не удален \n");
                    }
                }
            } else {
                logger.appendLogMessage(" Попытка удалить файл в (" + directory + "):нет файлов для удаления с расширением .dat \n");
            }
        } else {
            logger.appendLogMessage(" Попытка удалить файл в (" + directory + "): Каталог отсутствует \n");
        }
        logger.saveLogFile();
    }

    private static void saveGame(String filePath, GameProgress gameProgress) {
        try (FileOutputStream file = new FileOutputStream(filePath);
             ObjectOutputStream savedGameProgress = new ObjectOutputStream(file)) {
            savedGameProgress.writeObject(gameProgress);
            logger.appendLogMessage(" Попытка сохранить игру в файл (" + filePath + "): Игра сохранена\n");
        } catch (Exception ex) {
            logger.appendLogMessage(" Попытка сохранить игру в файл (" + filePath + "): ОШИБКА " + ex.getMessage() + "\n");
        }
        logger.saveLogFile();
    }

    private static void createZip(String zipPath) {
        File saveGameDirectory = new File(saveGamePath);
        if (saveGameDirectory.exists()) {
            File[] listFiles = saveGameDirectory.listFiles(new FileExtensionFilter(".dat"));
            if (listFiles != null) {
                try (ZipOutputStream directoryForPacking = new ZipOutputStream(new FileOutputStream(zipPath))) {
                    for (File file : listFiles) {
                        FileInputStream filePathToZip = new FileInputStream(file);
                        ZipEntry fileNameToZip = new ZipEntry(file.getName());
                        directoryForPacking.putNextEntry(fileNameToZip);
                        byte[] buffer = new byte[filePathToZip.available()];
                        if (filePathToZip.read(buffer) != -1) {
                            directoryForPacking.write(buffer);
                            logger.appendLogMessage(" Файл " + file.getName() + " добавлен в архив " + zipPath + "\n");
                        } else {
                            logger.appendLogMessage(" Файл " + file.getName() + " ошибка чтения, буфер = -1 Byte " + zipPath + "\n");
                        }
                        filePathToZip.close();
                    }
                    directoryForPacking.closeEntry();
                } catch (Exception ex) {
                    logger.appendLogMessage(" Попытка упаковать файл завершена с ошибкой " + ex.getMessage() + "\n");
                }
            } else {
                logger.appendLogMessage(" Попытка упаковать сохранения в (" + saveGamePath + "): ОШИБКА файлы с расширением .dat не найдены\n");
            }
        }
        logger.saveLogFile();
        deleteFile(saveGamePath, ".dat");
    }


    private static void runInstall() {
        logger.appendLogMessage(" =======СТАРТ ИНСТАЛЯЦИИ=======\n");
        dirList.forEach((path, childrenPath) -> {
            createFolder(path);
            if (!childrenPath.isEmpty()) {
                for (String folder : childrenPath) {
                    createFolder(path + folder);
                }
            }
        });
        logger.appendLogMessage(" =======ИНСТАЛЯЦИЯ ЗАВЕРШЕНА=======\n");
        logger.saveLogFile();
    }

    private static void createFolder(String folderPath) {
        File directoryName = new File(folderPath);
        if (folderPath.contains(".")) {
            createFile(folderPath);
            return;
        }
        if (directoryName.exists()) {
            logger.appendLogMessage(" Попытка создать каталог(" + folderPath + "): Каталог уже существует\n");
        } else {
            if (directoryName.mkdir()) {
                logger.appendLogMessage(" Попытка создать каталог(" + folderPath + "): Каталог создан\n");
            } else {
                logger.appendLogMessage(" Попытка создать каталог(" + folderPath + "): Каталог НЕ создан код 52\n");
            }
        }
    }

    private static void createFile(String folderPath) {
        File filePath = new File(folderPath);
        try {
            if (filePath.createNewFile()) {
                logger.appendLogMessage(" Попытка создать файл(" + folderPath + "): Файл создан\n");
            } else {
                logger.appendLogMessage(" Попытка создать файл(" + folderPath + "): Файл уже существует\n");
            }
        } catch (IOException ex) {
            logger.appendLogMessage("Попытка создать файл(" + folderPath + ") завершена с ошибкой: " + ex.getMessage());
        }
    }
}
