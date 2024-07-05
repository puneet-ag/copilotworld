package com.dpworld.copilotworld.panel

import com.dpworld.copilotworld.avatar.AvatarBundle
import com.dpworld.copilotworld.avatar.AvatarKeys
import com.dpworld.copilotworld.configuration.ConfigurationSettings
import com.dpworld.copilotworld.configurations.GeneralSettings
import com.dpworld.copilotworld.util.EditorActionsUtil
import com.dpworld.copilotworld.util.OverlayUtil
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import io.ktor.util.*
import java.nio.file.Paths

class AvatarProjectActivity : ProjectActivity {

    private val watchExtensions = setOf("jpg", "jpeg", "png")

    override suspend fun execute(project: Project) {
        EditorActionsUtil.refreshActions()

        val settings = service<GeneralSettings>().state
        if (settings.selectedService == ServiceType.AVATAR) {
            //project.service<AvatarService>().syncUserDetailsAsync()
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
            AvatarBundle.get("imageAttachmentNotification.content"),
            NotificationType.INFORMATION
        )
            .addAction(NotificationAction.createSimpleExpiring(
                AvatarBundle.get("imageAttachmentNotification.action")
            ) {
                AvatarKeys.IMAGE_ATTACHMENT_FILE_PATH.set(project, filePath)
                project.messageBus
                    .syncPublisher<AttachImageNotifier>(
                        AttachImageNotifier.IMAGE_ATTACHMENT_FILE_PATH_TOPIC
                    )
                    .imageAttached(filePath)
            })
            .addAction(NotificationAction.createSimpleExpiring(
                AvatarBundle.get("shared.notification.doNotShowAgain")
            ) {
                ConfigurationSettings.getCurrentState().isCheckForNewScreenshots = false
            })
            .notify(project)
    }
}