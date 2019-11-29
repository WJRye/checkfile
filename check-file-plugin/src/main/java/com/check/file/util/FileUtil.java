package com.check.file.util;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

public final class FileUtil {

    private FileUtil() {
    }

    /**
     * 通过文件名字找目标文件
     *
     * @param desFile        目标文件夹
     * @param targetFileName 目标文件名字
     * @return 目标文件
     */
    public static List<File> findTargetFileByName(File desFile, String targetFileName) {
        List<File> targetFile = new LinkedList<>();
        File[] files = desFile.listFiles();
        if (files != null) {
            LinkedList<File> listFiles = new LinkedList<>(Arrays.asList(files));
            while (!listFiles.isEmpty()) {
                File nextFile = listFiles.removeFirst();
                if (nextFile.isDirectory()) {
                    File[] childFiles = nextFile.listFiles();
                    if (childFiles != null) {
                        if (nextFile.getName().startsWith(targetFileName) && childFiles.length > 0) {
                            targetFile.add(nextFile);
                            continue;
                        }
                        for (File childFile : childFiles) {
                            if (childFile.isDirectory()) {
                                listFiles.add(childFile);
                            }
                        }
                    }
                }
            }
        }
        return targetFile;
    }


    /**
     * 在目标文件夹下找到对应大小的图片
     *
     * @param desFile 目标文件
     * @return 图片文件
     */
    public static List<File> findTargetImageBySize(File desFile) {
        List<File> targetFiles = new ArrayList<>();
        File[] files = desFile.listFiles();
        if (files != null) {
            ArrayList<File> listFiles = new ArrayList<>(Arrays.asList(files));
            for (File childFile : listFiles) {
                try {
                    Image image = ImageIO.read(childFile);
                    if (image != null) {
                        targetFiles.add(childFile);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return targetFiles;

    }
}