package com.dpworld.copilotworld.panel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IntellijFileUtil {
    private static final Logger LOG = Logger.getInstance(IntellijFileUtil.class);

    public static File createFile(Object directoryPath, String fileName, String fileContent) {
        Objects.requireNonNull(fileContent, "fileContent null");
        Objects.requireNonNull(fileName, "fileName null or blank");

        Path path;
        if (directoryPath instanceof Path) {
            path = (Path) directoryPath;
        } else if (directoryPath instanceof File) {
            path = ((File) directoryPath).toPath();
        } else if (directoryPath instanceof String) {
            path = Paths.get((String) directoryPath);
        } else {
            throw new IllegalArgumentException("directoryPath must be Path, File or String: " + directoryPath);
        }

        try {
            tryCreateDirectory(path);
            Files.writeString(path.resolve(fileName), fileContent, StandardOpenOption.CREATE);
            return path.resolve(fileName).toFile();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create file", e);
        }
    }


    public static VirtualFile getEditorFile(Editor editor) {
        return FileDocumentManager.getInstance().getFile(editor.getDocument());
    }

    private static void tryCreateDirectory(Path directoryPath) {
        if (!Files.exists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create directory: " + directoryPath, e);
            }
        }
    }

    public static String getFileExtension(String filename) {
        Pattern pattern = Pattern.compile("[^.]+$");
        Matcher matcher = pattern.matcher(filename);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    public static Map.Entry<String, String> findLanguageExtensionMapping(String language) {
        Map.Entry<String, String> defaultValue = Map.of("Text", ".txt").entrySet().iterator().next();
        ObjectMapper mapper = new ObjectMapper();
        List<FileExtensionLanguageDetails> extensionToLanguageMappings;
        List<LanguageFileExtensionDetails> languageToExtensionMappings;
        try {
            extensionToLanguageMappings = mapper.readValue(getResourceContent("/fileExtensionLanguageMappings.json"),
                    new TypeReference<List<FileExtensionLanguageDetails>>() {});
            languageToExtensionMappings = mapper.readValue(getResourceContent("/languageFileExtensionMappings.json"),
                    new TypeReference<List<LanguageFileExtensionDetails>>() {});
        } catch (JsonProcessingException e) {
            LOG.error("Unable to extract file extension", e);
            return defaultValue;
        }

        Optional<Map.Entry<String, String>> firstExtension = findFirstExtension(languageToExtensionMappings, language);
        if (firstExtension.isEmpty()) {
            return extensionToLanguageMappings.stream()
                    .filter(details -> details.extension().equalsIgnoreCase(language))
                    .findFirst()
                    .flatMap(details -> findFirstExtension(languageToExtensionMappings, details.value()))
                    .orElse(defaultValue);
        }
        return firstExtension.get();
    }

    public static boolean isUtf8File(String filePath) {
        try {
            Path path = Paths.get(filePath);
            int c;
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                c = reader.read();
                if (c >= 0) {
                    reader.transferTo(Writer.nullWriter());
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getImageMediaType(String fileName) {
        String fileExtension = getFileExtension(fileName);
        switch (fileExtension) {
            case "png":
                return "image/png";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            default:
                throw new IllegalArgumentException("Unsupported image type: " + fileExtension);
        }
    }

    public static String getResourceContent(String name) {
        try (InputStream stream = Objects.requireNonNull(IntellijFileUtil.class.getResourceAsStream(name))) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read resource", e);
        }
    }

    public static String convertFileSize(long fileSizeInBytes) {
        String[] units = {"B", "KB", "MB", "GB"};
        int unitIndex = 0;
        double fileSize = fileSizeInBytes;

        while (fileSize >= 1024 && unitIndex < units.length - 1) {
            fileSize /= 1024.0;
            unitIndex++;
        }

        return new DecimalFormat("#.##").format(fileSize) + " " + units[unitIndex];
    }

    public static String convertLongValue(long value) {
        if (value >= 1000000) {
            return (value / 1000000) + "M";
        }
        if (value >= 1000) {
            return (value / 1000) + "K";
        }
        return Long.toString(value);
    }

    public static Optional<Map.Entry<String, String>> findFirstExtension(List<LanguageFileExtensionDetails> languageFileExtensionMappings, String language) {
        return languageFileExtensionMappings.stream()
                .filter(details -> language.equalsIgnoreCase(details.name())
                        && details.extensions() != null
                        && details.extensions().stream().anyMatch(Objects::nonNull))
                .findFirst()
                .map(details -> new AbstractMap.SimpleEntry<>(details.name(),
                        details.extensions().stream().filter(Objects::nonNull).findFirst().orElse("")));
    }
}

