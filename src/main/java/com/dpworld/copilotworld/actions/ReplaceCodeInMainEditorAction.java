package com.dpworld.copilotworld.actions;

import com.dpworld.copilotworld.util.EditorActionsUtil;
import com.dpworld.copilotworld.util.CodeEditorHelper;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public class ReplaceCodeInMainEditorAction extends AnAction {

  public ReplaceCodeInMainEditorAction() {
    super("Replace in Main Editor", "Replace code in main editor", AllIcons.Actions.Replace);
    EditorActionsUtil.registerAction(this);
  }

  @Override
  public void update(@NotNull AnActionEvent event) {
    event.getPresentation().setEnabled(
        CodeEditorHelper.isSelectedTextInMainEditor(requireNonNull(event.getProject()))
            && CodeEditorHelper.editorHasSelection(event.getData(PlatformDataKeys.EDITOR)));
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent event) {
    var project = event.getProject();
    var toolWindowEditor = event.getData(PlatformDataKeys.EDITOR);
    if (project != null && toolWindowEditor != null) {
      CodeEditorHelper.replaceSelectionInMainEditor(
          project,
          requireNonNull(toolWindowEditor.getSelectionModel().getSelectedText()));
    }
  }

  @Override
  public @NotNull ActionUpdateThread getActionUpdateThread() {
    return ActionUpdateThread.EDT;
  }
}
