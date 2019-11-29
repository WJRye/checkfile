package com.check.file.task;

import com.check.file.CheckFileConstants;
import com.check.file.util.LoggerUtil;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;

import java.io.File;
import java.util.Collection;
import java.util.Locale;

public abstract class BaseDefaultTask extends DefaultTask {

    /**
     * 换算成K
     */
    private static final long SIZE_UNIT = 1000;

    /**
     * 日志名称
     */
    static final String TAG = CheckFileConstants.LOG;

    /**
     * 打印日志工具
     */
    static final Logger LOGGER = LoggerUtil.getLogger(TAG);

    /**
     * 打印消息
     */
    private static final String FOLLOW_MSG = "Files larger than the maximum size(%d KB) are as follows:";
    /**
     * 打印消息
     */
    private static final String FILE_MSG = "w: %s(size=%.2f KB)";


    @TaskAction
    public abstract void checkFileTask();


    void checkFile(Collection<File> targetFiles, String projectName, long size) {
        if (!targetFiles.isEmpty()) {
            boolean hasLargerFile = false;
            float targetFileSize = 0f;
            for (File targetFile : targetFiles) {
                targetFileSize = (float) targetFile.length() / SIZE_UNIT;
                if (targetFileSize > size) {
                    if (!hasLargerFile) {
                        hasLargerFile = true;
                        LOGGER.warn(TAG + projectName);
                        LOGGER.warn(String.format(Locale.getDefault(), FOLLOW_MSG, size));
                    }
                    LOGGER.warn(String.format(Locale.getDefault(), FILE_MSG, targetFile.getPath(), targetFileSize));
                }
            }
        }
    }
}