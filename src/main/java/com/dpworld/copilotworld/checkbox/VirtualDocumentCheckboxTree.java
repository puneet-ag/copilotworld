package com.dpworld.copilotworld.checkbox;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.CheckedTreeNode;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;
import com.dpworld.copilotworld.panel.ReferencedFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class VirtualDocumentCheckboxTree extends DocumentCheckboxTree {

  public VirtualDocumentCheckboxTree(@NotNull VirtualFile[] rootFiles) {
    super(createFileTypesRenderer(), createRootNode(rootFiles));
  }

  public List<ReferencedFile> fetchReferencedFiles() {
    var checkedNodes = getCheckedNodes(VirtualFile.class, Objects::nonNull);
    if (checkedNodes.length > 1000) {
      throw new RuntimeException("Too many files selected");
    }

    return Arrays.stream(checkedNodes)
        .map(item -> new ReferencedFile(new File(item.getPath())))
        .toList();
  }

  private static CheckedTreeNode createRootNode(VirtualFile[] files) {
    CheckedTreeNode rootNode = new CheckedTreeNode(null);
    for (VirtualFile file : files) {
      rootNode.add(createNode(file));
    }
    return rootNode;
  }

  private static CheckedTreeNode createNode(VirtualFile file) {
    CheckedTreeNode node = new CheckedTreeNode(file);
    if (file.isDirectory()) {
      VirtualFile[] children = file.getChildren();
      for (VirtualFile child : children) {
        node.add(createNode(child));
      }
    }
    return node;
  }

  private static @NotNull DocumentCheckboxTreeCellRenderer createFileTypesRenderer() {
    return new DocumentCheckboxTreeCellRenderer() {
      @Override
      void updatePresentation(Object userObject) {
        if (userObject instanceof VirtualFile virtualFile) {
          if (virtualFile.isDirectory()) {
            getTextRenderer().append(virtualFile.getName());
            getTextRenderer().setIcon(PlatformIcons.FOLDER_ICON);
          } else {
            renderDocumentDetails(getTextRenderer(), virtualFile);
          }
        } else if (userObject == null) {
          getTextRenderer().setIcon(AllIcons.Nodes.Folder);
          getTextRenderer().append("[root]");
        }
      }
    };
  }
}