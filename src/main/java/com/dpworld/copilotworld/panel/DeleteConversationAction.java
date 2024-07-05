package com.dpworld.copilotworld.panel;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class DeleteConversationAction extends AnAction {

  private final Runnable onDelete;

  public DeleteConversationAction(Runnable onDelete) {
    super("Delete Conversation", "Delete single conversation", AllIcons.Actions.GC);
    this.onDelete = onDelete;
    EditorActionsUtil.registerAction(this);
  }

  @Override
  public void update(@NotNull AnActionEvent event) {
    event.getPresentation().setEnabled(ConversationsState.getCurrentConversation() != null);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent event) {
    if (OverlayUtil.showDeleteConversationDialog() == Messages.YES) {
      var project = event.getProject();
      if (project != null) {
        onDelete.run();
      }
    }
  }

  @Override
  public @NotNull ActionUpdateThread getActionUpdateThread() {
    return ActionUpdateThread.EDT;
  }
}
