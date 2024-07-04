package com.dpworld.copilotworld.actions;

import com.intellij.icons.AllIcons.Actions;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;

import org.jetbrains.annotations.NotNull;
import com.dpworld.copilotworld.panel.converted.OverlayUtil;
import com.dpworld.copilotworld.panel.converted.VisionBundle;
import com.dpworld.copilotworld.panel.converted.EditorUtil;

import static java.util.Objects.requireNonNull;

public class ReplaceSelectionAction extends TrackableAction {

  public ReplaceSelectionAction(@NotNull Editor editor) {
    super(
        editor,
        VisionBundle.get("toolwindow.chat.editor.action.replaceSelection.title"),
        VisionBundle.get("toolwindow.chat.editor.action.replaceSelection.description"),
        Actions.Replace,
        ActionType.REPLACE_IN_MAIN_EDITOR);
  }

  @Override
  public void handleAction(@NotNull AnActionEvent event) {
    var project = requireNonNull(event.getProject());
    if (EditorUtil.isMainEditorTextSelected(project)) {
      EditorUtil.replaceMainEditorSelection(project, editor.getDocument().getText());
    } else {
      OverlayUtil.showSelectedEditorSelectionWarning(event);
    }
  }
}
