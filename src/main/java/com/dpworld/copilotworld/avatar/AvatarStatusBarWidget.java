package com.dpworld.copilotworld.avatar;

import com.dpworld.copilotworld.panel.Icons;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupFactory.ActionSelectionAid;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.impl.status.EditorBasedStatusBarPopup;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AvatarStatusBarWidget extends EditorBasedStatusBarPopup {

  public AvatarStatusBarWidget(Project project) {
    super(project, false);
  }

  @Override
  protected @NotNull WidgetState getWidgetState(@Nullable VirtualFile file) {
    var state = new WidgetState(AvatarBundle.get("statusBar.widget.tooltip"), "", true);
    state.setIcon(Icons.DefaultSmall);
    return state;
  }

  @Override
  protected @Nullable ListPopup createPopup(@NotNull DataContext context) {
    return JBPopupFactory.getInstance()
        .createActionGroupPopup(
            AvatarBundle.get("project.label"),
            (ActionGroup) ActionManager.getInstance().getAction("avatar.statusBarPopup"),
            context,
            ActionSelectionAid.SPEEDSEARCH,
            true);
  }

  @Override
  protected @NotNull StatusBarWidget createInstance(@NotNull Project project) {
    return new AvatarStatusBarWidget(project);
  }

  @Override
  public @NonNls @NotNull String ID() {
    return "com.dpworld.copilotworld.panel.converted.statusbar.widget";
  }
}