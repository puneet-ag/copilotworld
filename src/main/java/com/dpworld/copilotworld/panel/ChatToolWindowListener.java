package com.dpworld.copilotworld.panel;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;

public class ChatToolWindowListener implements ToolWindowManagerListener {

    @Override
    public void toolWindowShown(ToolWindow toolWindow) {
        if ("Avatar".equals(toolWindow.getId())) {
            requestFocusForTextArea(toolWindow.getProject());
        }
    }

    private void requestFocusForTextArea(Project project) {
        ChatToolWindowContentManager contentManager = project.getService(ChatToolWindowContentManager.class);
        contentManager.tryFindChatTabbedPane().ifPresent(tabbedPane -> {
            tabbedPane.tryFindActiveTabPanel().ifPresent(tabPanel -> {
                tabPanel.requestFocusForTextArea();
            });
        });
    }
}

