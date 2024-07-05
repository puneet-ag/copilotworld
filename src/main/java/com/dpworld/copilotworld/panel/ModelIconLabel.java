package com.dpworld.copilotworld.panel;

import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBFont;

import javax.swing.*;

public class ModelIconLabel extends JBLabel {

  public ModelIconLabel(String clientCode, String modelCode) {
    if ("chat.completion".equals(clientCode)) {
      setIcon(Icons.Ollama);
    }
    setFont(JBFont.small());
    setHorizontalAlignment(SwingConstants.LEADING);
  }

}
