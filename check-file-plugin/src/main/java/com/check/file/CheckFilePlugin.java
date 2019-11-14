package com.check.file;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

import java.util.Collections;

public class CheckFilePlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getExtensions().create(CheckFileExtension.NAME, CheckFileExtension.class);
        Task checkDrawableTask = project.getTasks().create(CheckDrawableTask.NAME, CheckDrawableTask.class);
        Task assembleTask = project.getTasks().getByName("assemble");
        //运行assemble任务的时候运行check-drawable任务
        assembleTask.setDependsOn(Collections.singleton(checkDrawableTask));

        try {
            //运行lint任务的时候运行check-drawable任务
            Task lintTask = project.getTasks().getByName("lint");
            lintTask.setDependsOn(Collections.singleton(checkDrawableTask));
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }
}
