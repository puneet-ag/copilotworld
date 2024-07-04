package actions;

import com.intellij.icons.AllIcons.Actions;
import com.intellij.icons.AllIcons.Diff;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.ui.JBMenuItem;
import org.jetbrains.annotations.NotNull;
import panel.converted.CodeGPTBundle;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class EditAction extends AbstractAction {

  private final EditorEx editor;

  public EditAction(@NotNull EditorEx editor) {
    super("Edit Source", Actions.EditSource);
    this.editor = editor;
  }

  @Override
  public void actionPerformed(@NotNull ActionEvent event) {
    editor.setViewer(!editor.isViewer());

    var viewer = editor.isViewer();
    editor.setCaretVisible(!viewer);
    editor.setCaretEnabled(!viewer);

    var settings = editor.getSettings();
    settings.setCaretRowShown(!viewer);

    var menuItem = (JBMenuItem) event.getSource();
    menuItem.setText(viewer
        ? CodeGPTBundle.get("toolwindow.chat.editor.action.edit.title")
        : CodeGPTBundle.get("toolwindow.chat.editor.action.disableEditing.title"));
    menuItem.setIcon(viewer ? Actions.EditSource : Diff.Lock);
  }
}
