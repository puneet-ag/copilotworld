package com.dpworld.copilotworld.panel.converted;

import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.io.path.ExperimentalPathApi;
import kotlin.io.path.PathsKt;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

@ExperimentalPathApi
public class CodeGPTProjectActivity implements ProjectActivity {

    private final Set<String> watchExtensions = new HashSet<>(Set.of("jpg", "jpeg", "png"));

    @Override
    public Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        EditorActionsUtil.refreshActions();

        GeneralSettings settings = ServiceManager.getService(project, GeneralSettings.class);
        if (settings.getSelectedService() == ServiceType.CODEGPT) {
//            ServiceManager.getService(project, CodeGPTService.class).syncUserDetailsAsync();
        }

        if (!ApplicationManager.getApplication().isUnitTestMode()
                && ConfigurationSettings.getCurrentState().isCheckForNewScreenshots()) {
            Path desktopPath = Paths.get(System.getProperty("user.home"), "Desktop");
            ServiceManager.getService(project, FileWatcher.class).watch(desktopPath, file -> {
                if (watchExtensions.contains(PathsKt.getExtension(file).toLowerCase())) {
                    showImageAttachmentNotification(
                            project,
                            desktopPath.resolve(file).toAbsolutePath().toString()
                    );
                }
            });
        }
        return null;
    }

    private void showImageAttachmentNotification(Project project, String filePath) {
        OverlayUtil.getDefaultNotification(
                        CodeGPTBundle.get("imageAttachmentNotification.content"),
                        NotificationType.INFORMATION
                )
                .addAction(NotificationAction.createSimpleExpiring(
                        CodeGPTBundle.get("imageAttachmentNotification.action"),
                        () -> {
                            CodeGPTKeys.IMAGE_ATTACHMENT_FILE_PATH.set(project, filePath);
                            project.getMessageBus().syncPublisher(AttachImageNotifier.IMAGE_ATTACHMENT_FILE_PATH_TOPIC)
                                    .imageAttached(filePath);
                        }
                ))
                .addAction(NotificationAction.createSimpleExpiring(
                        CodeGPTBundle.get("shared.notification.doNotShowAgain"),
                        () -> ConfigurationSettings.getCurrentState().setCheckForNewScreenshots(false)
                ))
                .notify(project);
    }

}

