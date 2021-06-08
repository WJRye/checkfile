package com.check.file.task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class ThirdPackageInfo {

    private ProjectInfo projectInfo;

    private List<ConfigurationInfo> configurationInfoList;

    public ProjectInfo getProjectInfo() {
        return projectInfo;
    }

    public void setProjectInfo(ProjectInfo projectInfo) {
        this.projectInfo = projectInfo;
    }

    public List<ConfigurationInfo> getConfigurationInfoList() {
        return configurationInfoList;
    }

    public void setConfigurationInfoList(List<ConfigurationInfo> configurationInfoList) {
        this.configurationInfoList = configurationInfoList;
    }

    public void addConfigurationInfo(ConfigurationInfo configurationInfo) {
        if (this.configurationInfoList == null) {
            this.configurationInfoList = new ArrayList<>();
        }
        this.configurationInfoList.add(configurationInfo);
    }

    public static class ProjectInfo{
        private String name;
        private String path;
        private String dir;
        private String rootDir;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getDir() {
            return dir;
        }

        public void setDir(String dir) {
            this.dir = dir;
        }

        public String getRootDir() {
            return rootDir;
        }

        public void setRootDir(String rootDir) {
            this.rootDir = rootDir;
        }
    }

    public static class ConfigurationInfo {
        private String name;
        private List<DependencyInfo> dependencyInfoList;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<DependencyInfo> getDependencyInfoList() {
            return dependencyInfoList;
        }

        public void setDependencyInfoList(List<DependencyInfo> dependencyInfoList) {
            this.dependencyInfoList = dependencyInfoList;
        }

        public void addDependencyInfo(DependencyInfo dependencyInfo) {
            if (this.dependencyInfoList == null) {
                this.dependencyInfoList = new ArrayList<>();
            }
            this.dependencyInfoList.add(dependencyInfo);
        }
    }

    public static class DependencyInfo {
        private String group;
        private String name;
        private String version;
        private Set<File> files;


        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public Set<File> getFiles() {
            return files;
        }

        public void setFiles(Set<File> files) {
            this.files = files;
        }
    }
}
