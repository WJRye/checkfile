package com.check.file;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

import java.util.Collections;

public class CheckFilePlugin implements Plugin<Project> {

    private static final String ASSEMBLE_TASK_NAME = "assemble";
    private static final String LINT_TASK_NAME = "lint";

    @Override
    public void apply(Project project) {
        project.getExtensions().create(CheckFileExtension.NAME, CheckFileExtension.class);
        Task checkDrawableTask = project.getTasks().create(CheckDrawableTask.NAME, CheckDrawableTask.class);
        //运行assemble任务的时候运行check-drawable任务
        Task assembleTask = project.getTasks().getByName(ASSEMBLE_TASK_NAME);
        assembleTask.setDependsOn(Collections.singleton(checkDrawableTask));
        //运行lint任务的时候运行check-drawable任务
        Task lintTask = project.getTasks().getByName(LINT_TASK_NAME);
        lintTask.setDependsOn(Collections.singleton(checkDrawableTask));
    }
}
