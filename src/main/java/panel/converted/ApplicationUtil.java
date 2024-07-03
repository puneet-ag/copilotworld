package panel.converted;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.openapi.wm.IdeFrame;

public class ApplicationUtil {

    // Private constructor to prevent instantiation
    private ApplicationUtil() {
    }

    // Static instance for the singleton pattern
    private static final ApplicationUtil INSTANCE = new ApplicationUtil();

    // Static method to access the singleton instance
    public static ApplicationUtil getInstance() {
        return INSTANCE;
    }

    public static boolean isUnitTestingMode() {
        return ApplicationManager.getApplication() != null
                && ApplicationManager.getApplication().isUnitTestMode();
    }

    public static Project findCurrentProject() {
        IdeFrame frame = IdeFocusManager.getGlobalInstance().getLastFocusedFrame();
        Project project = frame != null ? frame.getProject() : null;
        if (isValidProject(project)) {
            return project;
        }
        return findProjectFromOpenProjects();
    }

    private static Project findProjectFromOpenProjects() {
        ProjectManager projectManager = ProjectManager.getInstance();
        Project[] openProjects = projectManager.getOpenProjects();
        for (Project project : openProjects) {
            if (isValidProject(project)) {
                return project;
            }
        }
        return null;
    }

    private static boolean isValidProject(Project project) {
        return project != null && !project.isDisposed() && !project.isDefault();
    }
}

