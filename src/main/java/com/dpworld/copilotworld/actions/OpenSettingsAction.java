package com.dpworld.copilotworld.actions;

import com.dpworld.copilotworld.avatar.AvatarBundle;
import com.dpworld.copilotworld.panel.ServiceConfigurable;
import com.intellij.icons.AllIcons.General;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;

import org.jetbrains.annotations.NotNull;

public class OpenSettingsAction extends AnAction {

  public OpenSettingsAction() {
    super(AvatarBundle.get("action.openSettings.title"),
        AvatarBundle.get("action.openSettings.description"),
        General.Settings);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    ShowSettingsUtil.getInstance().showSettingsDialog(e.getProject(), ServiceConfigurable.class);
  }
}
