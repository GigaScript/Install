package com.company;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class InstallFileStructure {
    private String baseDir;
    private LinkedHashMap<String, LinkedList<String>> dirList = new LinkedHashMap<>();

    public InstallFileStructure(String baseDir) {
        this.baseDir = baseDir;
        dirList.put(baseDir, new LinkedList<>(Arrays.asList(
                "/src", "/res", "/savegames", "/temp"
        )));
        dirList.put(baseDir + "/src", new LinkedList<>(Arrays.asList(
                "/main", "/test"
        )));
        dirList.put(baseDir + "/src/main", new LinkedList<>(Arrays.asList(
                "/Main.java", "/Utils.java"
        )));
        dirList.put(baseDir + "/res", new LinkedList<>(Arrays.asList(
                "/drawables", "/vectors", "/icons"
        )));
        dirList.put(baseDir + "/temp", new LinkedList<>(Arrays.asList(
                "/temp.txt"
        )));
    }

    public LinkedHashMap<String, LinkedList<String>> getDirList() {
        return dirList;
    }
}
