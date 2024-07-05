package com.dpworld.copilotworld.panel;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.vfs.VirtualFile;

public class AttachImageAction extends AnAction {

    public AttachImageAction() {
        super(
                AvatarBundle.get("action.attachImage"),
                AvatarBundle.get("action.attachImageDescription"),
                Icons.Upload
        );
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        FileChooser.chooseFiles(createSingleImageFileDescriptor(), e.getProject(), null, files -> {
            if (!files.isEmpty()) {
                if (files.size() != 1) {
                    throw new IllegalStateException("Expected exactly one file to be selected");
                }
                VirtualFile file = files.get(0);
                if (e.getProject() != null) {
                    AvatarKeys.IMAGE_ATTACHMENT_FILE_PATH.set(e.getProject(), file.getPath());
                    e.getProject().getMessageBus()
                            .syncPublisher(AttachImageNotifier.IMAGE_ATTACHMENT_FILE_PATH_TOPIC)
                            .imageAttached(file.getPath());
                }
            }
        });
    }

    private FileChooserDescriptor createSingleImageFileDescriptor() {
        return new FileChooserDescriptor(
                true, false, false, false, false, false
        ).withFileFilter(file -> {
            String extension = file.getExtension();
            return "jpg".equalsIgnoreCase(extension) ||
                    "jpeg".equalsIgnoreCase(extension) ||
                    "png".equalsIgnoreCase(extension);
        }).withTitle(AvatarBundle.get("imageFileChooser.title"));
    }
}
