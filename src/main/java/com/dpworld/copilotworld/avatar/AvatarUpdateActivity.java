package com.dpworld.copilotworld.avatar;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import com.intellij.openapi.updateSettings.impl.UpdateChecker;
import com.intellij.openapi.updateSettings.impl.UpdateSettings;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AvatarUpdateActivity implements ProjectActivity {

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

        private void installAvatarUpdate(Project project) {
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

