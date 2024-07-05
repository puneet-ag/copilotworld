package com.dpworld.copilotworld.actions;

import com.dpworld.copilotworld.util.EditorActionsUtil;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsActions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class BaseEditorAction extends AnAction {

  BaseEditorAction(
      @Nullable @NlsActions.ActionText String text,
      @Nullable @NlsActions.ActionDescription String description,
      @Nullable Icon icon) {
    super(text, description, icon);
    EditorActionsUtil.registerAction(this);
  }

  public BaseEditorAction(
      @Nullable @NlsActions.ActionText String text,
      @Nullable @NlsActions.ActionDescription String description) {
    this(text, description, null);
  }

  protected abstract void actionPerformed(Project project, Editor editor, String selectedText);

  public void actionPerformed(@NotNull AnActionEvent event) {
    var project = event.getProject();
    var editor = event.getData(PlatformDataKeys.EDITOR);
    if (editor != null && project != null) {
      actionPerformed(project, editor, editor.getSelectionModel().getSelectedText());
    }
  }

  public void update(AnActionEvent event) {
    Project project = event.getProject();
    Editor editor = event.getData(PlatformDataKeys.EDITOR);
    boolean menuAllowed = false;
    if (editor != null && project != null) {
      menuAllowed = editor.getSelectionModel().getSelectedText() != null;
    }
    event.getPresentation().setEnabled(menuAllowed);
  }

  @Override
  public @NotNull ActionUpdateThread getActionUpdateThread() {
    return ActionUpdateThread.EDT;
  }
}
