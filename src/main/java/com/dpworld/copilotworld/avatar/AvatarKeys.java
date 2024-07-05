package com.dpworld.copilotworld.avatar;

import com.dpworld.copilotworld.panel.ReferencedFile;
import com.intellij.openapi.util.Key;

import java.util.List;

public class AvatarKeys {

  public static final Key<String> PREVIOUS_INLAY_TEXT =
      Key.create("avatar.editor.inlay.prev-value");
  public static final Key<List<ReferencedFile>> SELECTED_FILES =
      Key.create("avatar.selectedFiles");
  public static final Key<String> IMAGE_ATTACHMENT_FILE_PATH =
      Key.create("avatar.imageAttachmentFilePath");
  public static final Key<AvatarUserDetails> AVATAR_USER_DETAILS =
      Key.create("avatar.userDetails");
}
