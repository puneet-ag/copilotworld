package com.dpworld.copilotworld.panel.converted;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import static com.dpworld.copilotworld.panel.converted.Icons.Default;


public class DeleteAllConversationsAction extends AnAction {

  private final Runnable onRefresh;

  public DeleteAllConversationsAction(Runnable onRefresh) {
    super("Delete All", "Delete all conversations", AllIcons.Actions.GC);
    this.onRefresh = onRefresh;
    EditorActionsUtil.registerAction(this);
  }

  @Override
  public void update(@NotNull AnActionEvent event) {
    var project = event.getProject();
    if (project != null) {
      var sortedConversations = ConversationService.getInstance().getSortedConversations();
      event.getPresentation().setEnabled(!sortedConversations.isEmpty());
    }
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent event) {
    int answer = Messages.showYesNoDialog(
        "Are you sure you want to delete all conversations?",
        "Clear History",
        Default);
    if (answer == Messages.YES) {
      var project = event.getProject();
      if (project != null) {
        try {
          ConversationService.getInstance().clearAll();
          project.getService(ChatToolWindowContentManager.class).resetAll();
        } finally {
        }
      }
      this.onRefresh.run();
    }
  }

  @Override
  public @NotNull ActionUpdateThread getActionUpdateThread() {
    return ActionUpdateThread.BGT;
  }
}
