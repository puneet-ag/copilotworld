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

public class MarkdownUtil {

    
    public static List<String> splitCodeBlocks(String inputMarkdown) {
        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?s)```.*?```");
        Matcher matcher = pattern.matcher(inputMarkdown);
        int start = 0;
        while (matcher.find()) {
            result.add(inputMarkdown.substring(start, matcher.start()));
            result.add(matcher.group());
            start = matcher.end();
        }
        result.add(inputMarkdown.substring(start));
        return result.stream()
                .filter(str -> !str.isBlank())
                .collect(Collectors.toList());
    }

    public static String convertMdToHtml(String message) {
        MutableDataSet options = new MutableDataSet();
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options)
                .nodeRendererFactory(new ResponseNodeRenderer.Factory())
                .build();
        return renderer.render(parser.parse(message));
    }
}

