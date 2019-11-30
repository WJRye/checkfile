package com.check.file.task;

import com.check.file.CheckFileExtension;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ExternalDependency;
import org.gradle.api.artifacts.FileCollectionDependency;
import org.gradle.api.artifacts.ProjectDependency;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class CheckThirdPackageTask extends BaseDefaultTask {

    /**
     * 任务名字
     */
    public static final String NAME = "check-third-package";


    @Override
    public void checkFileTask() {
        CheckFileExtension checkFileExtension = (CheckFileExtension) getProject().getExtensions().getByName(CheckFileExtension.NAME);
        long thirdPackageMaxSize = checkFileExtension.thirdPackageMaxSize > 0 ? checkFileExtension.thirdPackageMaxSize : checkFileExtension.maxSize;
        LOGGER.warn(TAG + "maxSize=" + thirdPackageMaxSize + ";enable=" + checkFileExtension.enable);
        if (!checkFileExtension.enable || thirdPackageMaxSize <= 0) return;

        LinkedList<Project> projects = new LinkedList<>();
        projects.add(getProject());
        Set<String> allProjectNames = new HashSet<>();
        while (!projects.isEmpty()) {
            Project project = projects.removeFirst();
            Set<File> files = new HashSet<>();
            ConfigurationContainer configurations = project.getConfigurations();
            Iterator<Configuration> configurationIterator = configurations.iterator();
            while (configurationIterator.hasNext()) {
                Configuration configuration = configurationIterator.next();
                Iterator<Dependency> dependencyIterator = configuration.getDependencies().iterator();
                while (dependencyIterator.hasNext()) {
                    //获得主Module依赖的其它Module
                    Dependency dependency = dependencyIterator.next();
                    //ClientModule, ExternalDependency, ExternalModuleDependency, FileCollectionDependency, ModuleDependency, ProjectDependency, SelfResolvingDependency
                    if (dependency instanceof ProjectDependency) {
                        Project dependencyProject = ((ProjectDependency) dependency).getDependencyProject();
                        if (!allProjectNames.contains(dependencyProject.getName())) {
                            allProjectNames.add(dependencyProject.getName());
                            projects.add(dependencyProject);
                        }
                    } else if (dependency instanceof FileCollectionDependency) {
                        if (dependency.getGroup() != null) {
                            files.addAll(((FileCollectionDependency) dependency).getFiles().getFiles());
                        }
                    }
                }
            }
            checkFile(files, project.getName(), thirdPackageMaxSize);
        }
    }
}
