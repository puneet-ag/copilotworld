package com.dpworld.copilotworld.panel.converted;

import com.dpworld.copilotworld.activity.Conversation;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class MoveUpAction extends MoveAction {

  public MoveUpAction(Runnable onRefresh) {
    super("Move Up", "Move up", AllIcons.Actions.MoveUp, onRefresh);
    EditorActionsUtil.registerAction(this);
  }

  @Override
  protected Optional<Conversation> getConversation(@NotNull Project project) {
    return ConversationService.getInstance().getNextConversation();
  }
}
