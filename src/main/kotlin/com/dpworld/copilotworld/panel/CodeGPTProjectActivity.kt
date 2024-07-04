package com.dpworld.copilotworld.panel

import com.dpworld.copilotworld.panel.converted.*
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import io.ktor.util.*
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

class CodeGPTProjectActivity : ProjectActivity {

    private val watchExtensions = setOf("jpg", "jpeg", "png")

    override suspend fun execute(project: Project) {
        EditorActionsUtil.refreshActions()

        val settings = service<GeneralSettings>().state
        if (settings.selectedService == ServiceType.CODEGPT) {
            //project.service<CodeGPTService>().syncUserDetailsAsync()
        }

        if (!ApplicationManager.getApplication().isUnitTestMode
            && ConfigurationSettings.getCurrentState().isCheckForNewScreenshots
        ) {
            val desktopPath = Paths.get(System.getProperty("user.home"), "Desktop")
            project.service<FileWatcher>().watch(desktopPath) {
                if (watchExtensions.contains(it.extension.toLowerCase())) {
                    showImageAttachmentNotification(
                        project,
                        desktopPath.resolve(it).toAbsolutePath().toString()
                    )
                }
            }
        }
    }

    private fun showImageAttachmentNotification(project: Project, filePath: String) {
        OverlayUtil.getDefaultNotification(
            CodeGPTBundle.get("imageAttachmentNotification.content"),
            NotificationType.INFORMATION
        )
            .addAction(NotificationAction.createSimpleExpiring(
                CodeGPTBundle.get("imageAttachmentNotification.action")
            ) {
                CodeGPTKeys.IMAGE_ATTACHMENT_FILE_PATH.set(project, filePath)
                project.messageBus
                    .syncPublisher<AttachImageNotifier>(
                        AttachImageNotifier.IMAGE_ATTACHMENT_FILE_PATH_TOPIC
                    )
                    .imageAttached(filePath)
            })
            .addAction(NotificationAction.createSimpleExpiring(
                CodeGPTBundle.get("shared.notification.doNotShowAgain")
            ) {
                ConfigurationSettings.getCurrentState().isCheckForNewScreenshots = false
            })
            .notify(project)
    }
}
