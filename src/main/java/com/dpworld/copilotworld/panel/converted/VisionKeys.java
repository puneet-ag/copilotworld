package com.dpworld.copilotworld.panel.converted;

import com.intellij.openapi.util.Key;

import java.util.List;

public class VisionKeys {

  public static final Key<String> PREVIOUS_INLAY_TEXT =
      Key.create("vision.editor.inlay.prev-value");
  public static final Key<List<ReferencedFile>> SELECTED_FILES =
      Key.create("vision.selectedFiles");
  public static final Key<String> IMAGE_ATTACHMENT_FILE_PATH =
      Key.create("vision.imageAttachmentFilePath");
  public static final Key<VisionUserDetails> VISION_USER_DETAILS =
      Key.create("vision.userDetails");
}
