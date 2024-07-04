package actions;

import com.intellij.icons.AllIcons.Actions;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;

import org.jetbrains.annotations.NotNull;
import panel.converted.OverlayUtil;
import panel.converted.CodeGPTBundle;
import panel.converted.EditorUtil;

import static java.util.Objects.requireNonNull;

public class ReplaceSelectionAction extends TrackableAction {

  public ReplaceSelectionAction(@NotNull Editor editor) {
    super(
        editor,
        CodeGPTBundle.get("toolwindow.chat.editor.action.replaceSelection.title"),
        CodeGPTBundle.get("toolwindow.chat.editor.action.replaceSelection.description"),
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
