package com.dpworld.copilotworld.actions;

import com.dpworld.copilotworld.activity.Message;
import com.dpworld.copilotworld.conversation.chat.ChatToolWindowContentManager;
import com.dpworld.copilotworld.util.EditorActionsUtil;
import com.dpworld.copilotworld.util.IntellijFileUtil;
import com.dpworld.copilotworld.util.UIUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UI;

import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static java.lang.String.format;

public class CustomPromptAction extends BaseEditorAction {

  private static String previousUserPrompt = "";

  public CustomPromptAction() {
    super("Custom Prompt", "Custom prompt description", AllIcons.Actions.Run_anything);
    EditorActionsUtil.registerAction(this);
  }

  @Override
  protected void actionPerformed(Project project, Editor editor, String selectedText) {
    if (selectedText != null && !selectedText.isEmpty()) {
      var fileExtension = IntellijFileUtil.getFileExtension(editor.getVirtualFile().getName());
      var dialog = new CustomPromptDialog(previousUserPrompt);
      if (dialog.showAndGet()) {
        previousUserPrompt = dialog.getUserPrompt();
        var message = new Message(
            format("%s%n```%s%n%s%n```", previousUserPrompt, fileExtension, selectedText));
        message.setUserMessage(previousUserPrompt);
        SwingUtilities.invokeLater(() ->
            project.getService(ChatToolWindowContentManager.class).sendMessage(message));
      }
    }
  }

  public static class CustomPromptDialog extends DialogWrapper {

    private final JTextArea userPromptTextArea;

    public CustomPromptDialog(String previousUserPrompt) {
      super(true);
      this.userPromptTextArea = new JTextArea(previousUserPrompt);
      this.userPromptTextArea.setCaretPosition(previousUserPrompt.length());
      setTitle("Custom Prompt");
      setSize(400, getRootPane().getPreferredSize().height);
      init();
    }

    @Nullable
    public JComponent getPreferredFocusedComponent() {
      return userPromptTextArea;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
      userPromptTextArea.setLineWrap(true);
      userPromptTextArea.setWrapStyleWord(true);
      userPromptTextArea.setMargin(JBUI.insets(5));
      UIUtil.addShiftEnterInputMap(userPromptTextArea, new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
          clickDefaultButton();
        }
      });

      return FormBuilder.createFormBuilder()
          .addComponent(UI.PanelFactory.panel(userPromptTextArea)
              .withLabel("Prefix:")
              .moveLabelOnTop()
              .withComment("Example: Find bugs in the following code")
              .createPanel())
          .getPanel();
    }

    public String getUserPrompt() {
      return userPromptTextArea.getText();
    }
  }
}
