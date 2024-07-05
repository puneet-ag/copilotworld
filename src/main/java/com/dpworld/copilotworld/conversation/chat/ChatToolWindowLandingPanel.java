package com.dpworld.copilotworld.conversation.chat;

import com.dpworld.copilotworld.actions.LandingPanelAction;
import com.dpworld.copilotworld.configuration.GeneralSettings;
import com.dpworld.copilotworld.panel.Icons;
import com.dpworld.copilotworld.panel.ResponsePanel;
import com.dpworld.copilotworld.util.UIUtil;
import com.intellij.ui.components.ActionLink;
import com.intellij.util.ui.JBUI;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatToolWindowLandingPanel extends ResponsePanel {

    public ChatToolWindowLandingPanel(OnActionCallback onAction) {
        addContent(createContent(onAction));
    }

    private JPanel createContent(OnActionCallback onAction) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(UIUtil.createTextPane(getWelcomeMessage(), false), BorderLayout.NORTH);
        panel.add(createActionsListPanel(onAction), BorderLayout.CENTER);
        panel.add(UIUtil.createTextPane(getCautionMessage(), false), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createActionsListPanel(OnActionCallback onAction) {
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.PAGE_AXIS));
        listPanel.setBorder(JBUI.Borders.emptyLeft(4));







        return listPanel;
    }

    private ActionLink createEditorActionLink(LandingPanelAction action, OnActionCallback onAction) {
        ActionLink actionLink = new ActionLink(action.getUserMessage(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                Point locationOnScreen = ((ActionLink) event.getSource()).getLocationOnScreen();
                onAction.onAction(action, locationOnScreen);
            }
        });
        actionLink.setIcon(Icons.Sparkle);
        return actionLink;
    }

    private String getWelcomeMessage() {
        return "<html>" +
                "<p style=\"margin-top: 4px; margin-bottom: 4px;\">" +
                "Hi <strong>" + GeneralSettings.getCurrentState().getDisplayName() + "</strong>, I'm Avatar! " +
                "I’m your AI powered assistant. Is there anything I can help you with ?" +
                "</p>" +
                "</html>";
    }

    private String getCautionMessage() {
        return "<html>" +
                "<p style=\"margin-top: 4px; margin-bottom: 4px;\">" +
                "Caution: I can make mistakes. Make sure to verify any generated code or suggestions." +
                "</p>" +
                "</html>";
    }

    
    public interface OnActionCallback {
        void onAction(LandingPanelAction action, Point point);
    }
}

