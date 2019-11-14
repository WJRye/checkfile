package com.check.file;

import com.check.file.util.FileUtil;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CheckDrawableTask extends DefaultTask {

    private static final long SIZE_UNIT = 1024;
    private static final String TARGET_FILE_NAME = "drawable-";
    static final String NAME = "check-drawable";

    private static final String TAG = CheckFileConstants.LOG + "checkDrawable: ";


    @TaskAction
    public void checkDrawable() {
        CheckFileExtension checkFileExtension = (CheckFileExtension) getProject().getExtensions().getByName(CheckFileExtension.NAME);
        System.out.println(TAG + "maxSize=" + checkFileExtension.maxSize + ";enable=" + checkFileExtension.enable);
        if (!checkFileExtension.enable || checkFileExtension.maxSize <= 0) return;

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
                Dependency dependency = dependencyIterator.next();
                if (group.equals(dependency.getGroup())) {
                    projectNames.add(dependency.getName());
                }
            }
        }

        File rootFile = project.getRootDir();
        long maxSize = checkFileExtension.maxSize * SIZE_UNIT;
        for (String proName : projectNames) {
            scanFile(rootFile, proName, maxSize, checkFileExtension.maxSize);
        }

    }

    private void scanFile(File rootFile, String projectName, long maxSize, long size) {
        String resName = getResName(projectName);
        File desFile = new File(rootFile, resName);
        List<File> files = FileUtil.findTargetFileByName(desFile, TARGET_FILE_NAME);
        for (File file : files) {
            List<File> targetFiles = FileUtil.findTargetImageBySize(file, maxSize);
            for (File targetFile : targetFiles) {
                System.out.println(TAG + "file=" + targetFile.getPath() + "(size=" + targetFile.length() / SIZE_UNIT + "K) exceeds max size(" + size + "K) limit.");
            }
        }
    }

    private String getResName(String projectName) {
        return projectName + File.separator + "src" + File.separator + "main" + File.separator + "res";
    }

}
