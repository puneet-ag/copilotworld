package com.dpworld.copilotworld.actions;

import com.dpworld.copilotworld.activity.Conversation;
import com.dpworld.copilotworld.conversation.ConversationService;
import com.dpworld.copilotworld.util.EditorActionsUtil;
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
