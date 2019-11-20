package com.check.file;

import com.check.file.util.FileUtil;
import com.check.file.util.LoggerUtil;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class CheckDrawableTask extends DefaultTask {

    /**
     * 换算成K
     */
    private static final long SIZE_UNIT = 1000;
    /**
     * 要扫面的目标文件名字
     */
    private static final String TARGET_FILE_NAME = "drawable-";
    /**
     * 任务名字
     */
    static final String NAME = "check-drawable";
    /**
     * 日志名称
     */
    private static final String TAG = CheckFileConstants.LOG + "CheckDrawable: ";
    /**
     * 打印日志工具
     */
    private static final Logger LOGGER = LoggerUtil.getLogger(TAG);
    /**
     * 打印消息
     */
    private static final String FOLLOW_MSG = "Files larger than the maximum size(%d KB) are as follows:";
    /**
     * 打印消息
     */
    private static final String FILE_MSG = "w: %s(size=%.2f KB)";


    @TaskAction
    public void checkDrawable() {
        CheckFileExtension checkFileExtension = (CheckFileExtension) getProject().getExtensions().getByName(CheckFileExtension.NAME);
        long drawableMaxSize = checkFileExtension.drawabeMaxSize > 0 ? checkFileExtension.drawabeMaxSize : checkFileExtension.maxSize;
        LOGGER.warn(TAG + "maxSize=" + drawableMaxSize + ";enable=" + checkFileExtension.enable);
        if (!checkFileExtension.enable || drawableMaxSize <= 0) return;

        Project project = getProject();
        String group = (String) project.getGroup();

        String projectName = project.getName();
        List<String> projectNames = new ArrayList<>();
        projectNames.add(projectName);

        ConfigurationContainer configurations = project.getConfigurations();
        Iterator<Configuration> configurationIterator = configurations.iterator();
        while (configurationIterator.hasNext()) {
            Configuration configuration = configurationIterator.next();
            Iterator<Dependency> dependencyIterator = configuration.getDependencies().iterator();
            while (dependencyIterator.hasNext()) {
                //获得主Module依赖的其它Module
                Dependency dependency = dependencyIterator.next();
                if (group.equals(dependency.getGroup())) {
                    projectNames.add(dependency.getName());
                }
            }
        }

        File rootFile = project.getRootDir();
        long maxSize = drawableMaxSize * SIZE_UNIT;
        for (String proName : projectNames) {
            scanFile(rootFile, proName, maxSize, drawableMaxSize);
        }

    }

    /**
     * 扫描项目中超过设置的文件最大大小的图片文件
     *
     * @param rootFile    项目根文件
     * @param projectName 资源文件路径
     * @param maxSize     设置的文件的最大大小（换算成单位K后的大小）
     * @param size        设置的文件的最大大小（原始大小）
     */
    private void scanFile(File rootFile, String projectName, long maxSize, long size) {
        String resName = getResName(projectName);
        File desFile = new File(rootFile, resName);
        List<File> files = FileUtil.findTargetFileByName(desFile, TARGET_FILE_NAME);
        if (files.isEmpty()) {
            return;
        }

        List<File> targetFiles = new LinkedList<>();
        for (File file : files) {
            targetFiles.addAll(FileUtil.findTargetImageBySize(file, maxSize));
        }
        if (!targetFiles.isEmpty()) {
            LOGGER.warn(TAG + projectName);
            LOGGER.warn(String.format(Locale.getDefault(), FOLLOW_MSG, size));
            for (File targetFile : targetFiles) {
                LOGGER.warn(String.format(Locale.getDefault(), FILE_MSG, targetFile.getPath(), (float) targetFile.length() / SIZE_UNIT));
            }
        }
    }

    /**
     * 获得项目下res文件路径（相对路径）
     *
     * @param projectName 项目名字
     * @return res文件路径（相对路径）
     */
    private static String getResName(String projectName) {
        return projectName + File.separator + "src" + File.separator + "main" + File.separator + "res";
    }

}
