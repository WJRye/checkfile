package com.check.file.task;

import com.check.file.CheckFileExtension;
import com.check.file.util.FileUtil;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ProjectDependency;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class CheckDrawableTask extends BaseDefaultTask {


    /**
     * 要扫面的目标文件名字
     */
    private static final String TARGET_FILE_NAME = "drawable-";
    /**
     * 任务名字
     */
    public static final String NAME = "check-drawable";

    @Override
    public void checkFileTask() {
        CheckFileExtension checkFileExtension = (CheckFileExtension) getProject().getExtensions().getByName(CheckFileExtension.NAME);
        long drawableMaxSize = checkFileExtension.drawableMaxSize > 0 ? checkFileExtension.drawableMaxSize : checkFileExtension.maxSize;
        LOGGER.warn(TAG + "maxSize=" + drawableMaxSize + ";enable=" + checkFileExtension.enable);
        if (!checkFileExtension.enable || drawableMaxSize <= 0) return;

        Project project = getProject();

        String projectName = project.getName();
        Set<String> projectNames = new LinkedHashSet<>();
        projectNames.add(projectName);

        File rootFile = project.getRootDir();
        ConfigurationContainer configurations = project.getConfigurations();
        Iterator<Configuration> configurationIterator = configurations.iterator();
        while (configurationIterator.hasNext()) {
            Configuration configuration = configurationIterator.next();
            Iterator<Dependency> dependencyIterator = configuration.getDependencies().iterator();
            while (dependencyIterator.hasNext()) {
                //获得主Module依赖的其它Module
                Dependency dependency = dependencyIterator.next();
                if (dependency instanceof ProjectDependency) {
                    projectNames.add(((ProjectDependency) dependency).getDependencyProject().getName());
                }
            }
        }

        for (String proName : projectNames) {
            scanFile(rootFile, proName, drawableMaxSize);
        }

    }

    /**
     * 扫描项目中超过设置的文件最大大小的图片文件
     *
     * @param rootFile    项目根文件
     * @param projectName 资源文件路径
     * @param size        设置的文件的最大大小（原始大小）
     */
    private void scanFile(File rootFile, String projectName, long size) {
        String resName = getResName(projectName);
        File desFile = new File(rootFile, resName);
        List<File> files = FileUtil.findTargetFileByName(desFile, TARGET_FILE_NAME);
        if (files.isEmpty()) {
            return;
        }

        List<File> targetFiles = new LinkedList<>();
        for (File file : files) {
            targetFiles.addAll(FileUtil.findTargetImageBySize(file));
        }
        checkFile(targetFiles, projectName, size);
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
