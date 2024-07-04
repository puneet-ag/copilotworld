package com.dpworld.copilotworld.checkbox;

import com.intellij.ui.CheckboxTree;
import com.intellij.ui.CheckedTreeNode;

import javax.swing.*;

public abstract class FileCheckboxTreeCellRenderer extends CheckboxTree.CheckboxTreeCellRenderer {

  abstract void updatePresentation(Object userObject);

  @Override
  public void customizeRenderer(
      JTree tree,
      Object value,
      boolean selected,
      boolean expanded,
      boolean leaf,
      int row,
      boolean hasFocus) {
    if (!(value instanceof CheckedTreeNode)) {
      return;
    }

    updatePresentation(((CheckedTreeNode) value).getUserObject());
  }
}
