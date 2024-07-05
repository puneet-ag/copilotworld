package com.dpworld.copilotworld.actions;

import com.intellij.icons.AllIcons.Actions;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;

import org.jetbrains.annotations.NotNull;
import com.dpworld.copilotworld.panel.OverlayUtil;
import com.dpworld.copilotworld.panel.AvatarBundle;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;

public class CopyAction extends TrackableAction {

  public CopyAction(@NotNull Editor editor) {
    super(
        editor,
        AvatarBundle.get("toolwindow.chat.editor.action.copy.title"),
        AvatarBundle.get("toolwindow.chat.editor.action.copy.description"),
        Actions.Copy,
        ActionType.COPY_CODE);
  }

  @Override
  public void handleAction(@NotNull AnActionEvent event) {
    StringSelection stringSelection = new StringSelection(editor.getDocument().getText());
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(stringSelection, null);

    var mouseEvent = (MouseEvent) event.getInputEvent();
    if (mouseEvent != null) {
      var locationOnScreen = mouseEvent.getLocationOnScreen();
      locationOnScreen.y = locationOnScreen.y - 16;

      OverlayUtil.showInfoBalloon(
              AvatarBundle.get("toolwindow.chat.editor.action.copy.success"),
              locationOnScreen);
    }
  }
}
