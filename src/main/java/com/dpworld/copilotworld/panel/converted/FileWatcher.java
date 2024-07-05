package com.dpworld.copilotworld.panel.converted;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.Service;
import java.nio.file.*;

@Service(Service.Level.PROJECT)
public final class FileWatcher implements Disposable {

    private Thread fileMonitor;

    public void watch(Path pathToWatch, FileCreatedListener onFileCreated) {
        fileMonitor = pathToWatch.toFile().exists() ? startWatching(pathToWatch, onFileCreated) : null;
    }

    private Thread startWatching(Path pathToWatch, FileCreatedListener onFileCreated) {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            pathToWatch.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

            Thread watchThread = new Thread(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        WatchKey key = watchService.take();
                        for (WatchEvent<?> event : key.pollEvents()) {
                            if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                                Path createdFilePath = (Path) event.context();
                                onFileCreated.onFileCreated(createdFilePath);
                            }
                        }
                        key.reset();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            watchThread.start();
            return watchThread;
        } catch (Exception e) {
            return null; 
        }
    }

    @Override
    public void dispose() {
        if (fileMonitor != null) {
            fileMonitor.interrupt();
        }
    }

    public interface FileCreatedListener {
        void onFileCreated(Path createdFilePath);
    }
}

