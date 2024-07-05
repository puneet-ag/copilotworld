package com.dpworld.copilotworld.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.openapi.wm.IdeFrame;

public class ApplicationManagerUtil {

    private ApplicationManagerUtil() {
    }
    private static final ApplicationManagerUtil INSTANCE = new ApplicationManagerUtil();
    
    public static ApplicationManagerUtil getInstance() {
        return INSTANCE;
    }

    public static Project getCurrentProject() {
        IdeFrame frame = IdeFocusManager.getGlobalInstance().getLastFocusedFrame();
        Project project = frame != null ? frame.getProject() : null;
        if (isProjectValid(project)) {
            return project;
        }
        return findProjectFromAllOpenProjects();
    }

    private static Project findProjectFromAllOpenProjects() {
        ProjectManager projectManager = ProjectManager.getInstance();
        Project[] openProjects = projectManager.getOpenProjects();
        for (Project project : openProjects) {
            if (isProjectValid(project)) {
                return project;
            }
        }
        return null;
    }

    private static boolean isProjectValid(Project project) {
        return project != null && !project.isDisposed() && !project.isDefault();
    }
}

