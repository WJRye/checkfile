package com.check.file.task;

import com.check.file.CheckFileExtension;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.FileCollectionDependency;
import org.gradle.api.artifacts.ProjectDependency;
import org.gradle.api.artifacts.ResolvableDependencies;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class CheckThirdPackageTask extends BaseDefaultTask {

    /**
     * 任务名字
     */
    public static final String NAME = "check-third-package";


    /**
     * 获取gradle本地缓存的package的路径
     *
     * @return 本地package的路径
     */
    private String getThirdPackageLocalPath() {
        String gradlePath = getProject().getGradle().getGradleUserHomeDir().getAbsolutePath();
        return gradlePath + "caches" + File.separatorChar + "modules-2" + File.separatorChar + "files-2.1" + File.separatorChar;
    }


    @Override
    public void checkFileTask() {
        CheckFileExtension checkFileExtension = (CheckFileExtension) getProject().getExtensions().getByName(CheckFileExtension.NAME);
        long thirdPackageMaxSize = checkFileExtension.thirdPackageMaxSize > 0 ? checkFileExtension.thirdPackageMaxSize : checkFileExtension.maxSize;
        LOGGER.warn(TAG + "maxSize=" + thirdPackageMaxSize + ";enable=" + checkFileExtension.enable);
        if (!checkFileExtension.enable || thirdPackageMaxSize <= 0) return;
        String thirdPackageLocalPath = getThirdPackageLocalPath();
        LinkedList<Project> projects = new LinkedList<>();
        projects.add(getProject());
        Set<String> allProjectNames = new LinkedHashSet<>();
        List<ThirdPackageInfo> thirdPackageInfoList = new ArrayList<>();
        while (!projects.isEmpty()) {
            ThirdPackageInfo thirdPackageInfo = new ThirdPackageInfo();
            Project project = projects.removeFirst();
            ThirdPackageInfo.ProjectInfo projectInfo = new ThirdPackageInfo.ProjectInfo();
            projectInfo.setName(project.getName());
            projectInfo.setPath(project.getPath());
            projectInfo.setDir(project.getProjectDir().getPath());
            projectInfo.setRootDir(project.getRootDir().getPath());
            thirdPackageInfo.setProjectInfo(projectInfo);
            ConfigurationContainer configurations = project.getConfigurations();
            Iterator<Configuration> configurationIterator = configurations.iterator();
            while (configurationIterator.hasNext()) {
                ThirdPackageInfo.ConfigurationInfo configurationInfo = new ThirdPackageInfo.ConfigurationInfo();
                Configuration configuration = configurationIterator.next();
                if (configuration.isCanBeResolved()) {
                    configurationInfo.setName(configuration.getName());
                    ResolvableDependencies resolvableDependencies = configuration.getIncoming();
                    if (!isAvailableConfiguration(configuration.getName()))
                        continue;
                    Iterator<Dependency> dependencyIterator = resolvableDependencies.getDependencies().iterator();
                    while (dependencyIterator.hasNext()) {
                        ThirdPackageInfo.DependencyInfo dependencyInfo = new ThirdPackageInfo.DependencyInfo();
                        Dependency dependency = dependencyIterator.next();
                        if (dependency instanceof ProjectDependency) {
                            Project dependencyProject = ((ProjectDependency) dependency).getDependencyProject();
                            if (!allProjectNames.contains(dependencyProject.getName())) {
                                allProjectNames.add(dependencyProject.getName());
                                projects.add(dependencyProject);
                            }
                            continue;
                        } else if (dependency instanceof FileCollectionDependency) {
                            dependencyInfo.setFiles(((FileCollectionDependency) dependency).getFiles().getFiles());
                            Set<File> files = dependencyInfo.getFiles();
                            if (files != null) {
                                StringBuilder sb = new StringBuilder();
                                for (File file : files) {
                                    String path = file.getPath();
                                    sb.append(path.substring(project.getRootDir().getPath().length())).append(";");
                                    dependencyInfo.setName(sb.toString());
                                }
                            }
                            configurationInfo.addDependencyInfo(dependencyInfo);
                            continue;
                        }
                        dependencyInfo.setGroup(dependency.getGroup());
                        dependencyInfo.setName(dependency.getName());
                        dependencyInfo.setVersion(dependency.getVersion());
                        String group = dependency.getGroup();
                        if (!isWhiteGroupList(group)) {
                            Set<File> files = new LinkedHashSet<>();
                            Iterator<File> fileIterator = configuration.files(dependency).iterator();
                            while (fileIterator.hasNext()) {
                                File file = fileIterator.next();
                                String filePath = file.getPath();
                                String relativeFilePath = filePath.substring(thirdPackageLocalPath.length() + 1);
                                if (!isWhiteGroupList(relativeFilePath)) {
                                    files.add(file);
                                }
                            }
                            dependencyInfo.setFiles(files);
                        }
                        configurationInfo.addDependencyInfo(dependencyInfo);
                    }
                    thirdPackageInfo.addConfigurationInfo(configurationInfo);
                }
            }
            thirdPackageInfoList.add(thirdPackageInfo);
        }

        Iterator<ThirdPackageInfo> thirdPackageInfoIterator = thirdPackageInfoList.iterator();
        while (thirdPackageInfoIterator.hasNext()) {
            ThirdPackageInfo thirdPackageInfo = thirdPackageInfoIterator.next();
            List<ThirdPackageInfo.ConfigurationInfo> configurationInfoList = thirdPackageInfo.getConfigurationInfoList();
            if (configurationInfoList == null) {
                continue;
            }
            Iterator<ThirdPackageInfo.ConfigurationInfo> configurationInfoIterator = configurationInfoList.iterator();
            while (configurationInfoIterator.hasNext()) {
                ThirdPackageInfo.ConfigurationInfo configurationInfo = configurationInfoIterator.next();
                List<ThirdPackageInfo.DependencyInfo> dependencyInfoList = configurationInfo.getDependencyInfoList();
                if (dependencyInfoList == null) {
                    continue;
                }
                Iterator<ThirdPackageInfo.DependencyInfo> dependencyInfoIterator = dependencyInfoList.iterator();
                while (dependencyInfoIterator.hasNext()) {
                    ThirdPackageInfo.DependencyInfo dependencyInfo = dependencyInfoIterator.next();
                    checkFile(dependencyInfo.getFiles(), thirdPackageInfo.getProjectInfo().getPath(), thirdPackageMaxSize);
                }
            }
        }
    }

    /**
     * 可参考<a href="https://blog.csdn.net/Gdeer/article/details/104815986"/>
     * <p>
     * 检查当前的 configuration，目前只选取"releaseCompileClasspath"和"releaseRuntimeClasspath"
     *
     * @param name 当前的configuration name
     * @return 是否是想要的configuration，返回true表示是，否则不是
     */
    private static boolean isAvailableConfiguration(String name) {
        return "releaseCompileClasspath".equals(name);
    }

    /**
     * 如果是系统库或者官方库等，就不需要检测文件大小
     *
     * @param content group 名字
     * @return 返回true表示是系统或者官方库，否则不是
     */
    private static boolean isWhiteGroupList(String content) {
        if (content == null) return false;
        String[] whiteList = {"androidx", "android.arch", "org.jetbrains.kotlin", "com.android.support", "com.google.android", "com.google", "org.jetbrains", "com.android"};
        for (String s : whiteList) {
            if (content.startsWith(s)) {
                return true;
            }
        }
        return false;
    }
}
