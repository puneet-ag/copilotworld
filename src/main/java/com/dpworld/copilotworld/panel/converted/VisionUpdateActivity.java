package com.dpworld.copilotworld.panel.converted;

import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import com.intellij.openapi.updateSettings.impl.UpdateChecker;
import com.intellij.openapi.updateSettings.impl.UpdateSettings;
import com.intellij.util.concurrency.AppExecutorUtil;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.util.concurrent.TimeUnit;

public class VisionUpdateActivity implements ProjectActivity {

    @Nullable
    @Override
    public Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        if (ApplicationManager.getApplication().isUnitTestMode()) {
            return null;
        }
        return null;
    }

    private static class CheckForUpdatesTask extends Task.Backgroundable {

        protected CheckForUpdatesTask(Project project) {
            super(project, "test", true);
        }

        @Override
        public void run(ProgressIndicator indicator) {
        }

        private void installVisionUpdate(Project project) {
            UpdateSettings settingsCopy = new UpdateSettings();
            var settingsState = settingsCopy.getState();
            settingsState.copyFrom(UpdateSettings.getInstance().getState());
            settingsState.setCheckNeeded(true);
            settingsState.setPluginsCheckNeeded(true);
            settingsState.setShowWhatsNewEditor(true);
            settingsState.setThirdPartyPluginsAllowed(true);
            UpdateChecker.updateAndShowResult(project, settingsCopy);
        }
    }
}

