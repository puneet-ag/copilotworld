package com.dpworld.copilotworld.actions;

import com.dpworld.copilotworld.conversation.ConversationsState;
import com.dpworld.copilotworld.util.EditorActionsUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class ClearChatWindowAction extends AnAction {

  private final Runnable onActionPerformed;

  public ClearChatWindowAction(Runnable onActionPerformed) {
    super("Clear Window", "Clears a chat window", AllIcons.General.Reset);
    this.onActionPerformed = onActionPerformed;
    EditorActionsUtil.registerAction(this);
  }

  @Override
  public void update(@NotNull AnActionEvent event) {
    super.update(event);
    var currentConversation = ConversationsState.getCurrentConversation();
    var isEnabled = currentConversation != null && !currentConversation.getMessages().isEmpty();
    event.getPresentation().setEnabled(isEnabled);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent event) {
    try {
      onActionPerformed.run();
    } finally {
    }
  }

  @Override
  public @NotNull ActionUpdateThread getActionUpdateThread() {
    return ActionUpdateThread.BGT;
  }
}