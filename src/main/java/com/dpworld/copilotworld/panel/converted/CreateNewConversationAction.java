package com.dpworld.copilotworld.panel.converted;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class CreateNewConversationAction extends AnAction {

  private final Runnable onCreate;

  public CreateNewConversationAction(Runnable onCreate) {
    super("Create New Chat", "Create new chat", AllIcons.General.Add);
    this.onCreate = onCreate;
    EditorActionsUtil.registerAction(this);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent event) {
    try {
      var project = event.getProject();
      if (project != null) {
        onCreate.run();
      }
    } finally {

    }
  }
}
