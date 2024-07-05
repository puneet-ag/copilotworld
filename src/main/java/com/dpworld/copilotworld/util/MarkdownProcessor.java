package com.dpworld.copilotworld.util;

import com.dpworld.copilotworld.panel.ResponseNodeRenderer;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MarkdownProcessor {

    public static List<String> extractCodeBlocks(String markdownContent) {
        List<String> extractedBlocks = new ArrayList<>();
        Pattern codeBlockPattern = Pattern.compile("(?s)```.*?```");
        Matcher codeBlockMatcher = codeBlockPattern.matcher(markdownContent);
        int currentIndex = 0;
        while (codeBlockMatcher.find()) {
            extractedBlocks.add(markdownContent.substring(currentIndex, codeBlockMatcher.start()));
            extractedBlocks.add(codeBlockMatcher.group());
            currentIndex = codeBlockMatcher.end();
        }
        extractedBlocks.add(markdownContent.substring(currentIndex));
        return extractedBlocks.stream()
                .filter(content -> !content.isBlank())
                .collect(Collectors.toList());
    }

    public static String markdownToHtml(String markdownText) {
        MutableDataSet parsingOptions = new MutableDataSet();
        Parser markdownParser = Parser.builder(parsingOptions).build();
        HtmlRenderer htmlRenderer = HtmlRenderer.builder(parsingOptions)
                .nodeRendererFactory(new ResponseNodeRenderer.Factory())
                .build();
        return htmlRenderer.render(markdownParser.parse(markdownText));
    }
}

