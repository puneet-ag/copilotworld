package com.dpworld.copilotworld.panel.converted;

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
//        listPanel.add(Box.createVerticalStrut(4));
//        listPanel.add(createEditorActionLink(LandingPanelAction.EXPLAIN, onAction));
//        listPanel.add(Box.createVerticalStrut(4));
//        listPanel.add(createEditorActionLink(LandingPanelAction.WRITE_TESTS, onAction));
//        listPanel.add(Box.createVerticalStrut(4));
//        listPanel.add(createEditorActionLink(LandingPanelAction.FIND_BUGS, onAction));
//        listPanel.add(Box.createVerticalStrut(4));
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
                "Hi <strong>" + GeneralSettings.getCurrentState().getDisplayName() + "</strong>, I'm Vision! " +
                "Here to assist with coding. Feel free to ask me anything, though I recommend verifying critical details." +
                "</p>" +
                "</html>";
    }

    private String getCautionMessage() {
        return "<html>" +
                "<p style=\"margin-top: 4px; margin-bottom: 4px;\">" +
                "</p>" +
                "</html>";
    }

    // Callback interface to replace Kotlin's lambda function
    public interface OnActionCallback {
        void onAction(LandingPanelAction action, Point point);
    }
}

