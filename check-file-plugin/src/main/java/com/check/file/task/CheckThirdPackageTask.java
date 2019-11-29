package com.check.file.task;

import com.check.file.CheckFileExtension;
import com.check.file.task.BaseDefaultTask;
import com.check.file.util.FileUtil;
import com.check.file.util.LoggerUtil;

import org.gradle.BuildAdapter;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ClientModule;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ExternalDependency;
import org.gradle.api.artifacts.ExternalModuleDependency;
import org.gradle.api.artifacts.FileCollectionDependency;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.artifacts.ModuleVersionSelector;
import org.gradle.api.artifacts.ProjectDependency;
import org.gradle.api.artifacts.SelfResolvingDependency;
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency;
import org.gradle.api.internal.artifacts.dependencies.DefaultProjectDependency;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.impldep.it.unimi.dsi.fastutil.Hash;
import org.slf4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import groovy.lang.Closure;

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
                        files.addAll(((FileCollectionDependency) dependency).getFiles().getFiles());
                    }
                }
            }
            checkFile(files, project.getName(), thirdPackageMaxSize);
        }
    }
}
