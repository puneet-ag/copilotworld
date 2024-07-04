package com.dpworld.copilotworld.panel.converted;

import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBFont;

import javax.swing.*;

public class ModelIconLabel extends JBLabel {

  public ModelIconLabel(String clientCode, String modelCode) {


    if ("chat.completion".equals(clientCode)) {
      setIcon(Icons.Ollama);
    }

    //setText(formatModelName(modelCode));
    setFont(JBFont.small());
    setHorizontalAlignment(SwingConstants.LEADING);
  }

//  private String formatModelName(String modelCode) {
//    try {
//      return OpenAIChatCompletionModel.findByCode(modelCode).getDescription();
//    } catch (NoSuchElementException e) {
//      return modelCode;
//    }
//  }
}
