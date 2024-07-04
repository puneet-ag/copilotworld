package com.dpworld.copilotworld.panel.converted;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupFactory.ActionSelectionAid;
import com.intellij.ui.awt.RelativePoint;

import java.awt.MouseInfo;

public class ShowEditorActionGroupAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        ActionManager actionManager = ActionManager.getInstance();
        ActionGroup actionGroup = (ActionGroup) actionManager.getAction("action.editor.group.EditorActionGroup");
        JBPopupFactory.getInstance().createActionGroupPopup(
                VisionBundle.get("project.label"), actionGroup, e.getDataContext(),
                ActionSelectionAid.ALPHA_NUMBERING, true
        ).show(RelativePoint.fromScreen(MouseInfo.getPointerInfo().getLocation()));
    }
}

