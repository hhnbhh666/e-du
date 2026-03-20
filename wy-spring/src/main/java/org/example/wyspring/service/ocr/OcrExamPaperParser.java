package org.example.wyspring.service.ocr;

import org.apache.commons.lang3.StringUtils;
import org.example.wyspring.vo.ocr.OcrQuestionItemVO;
import org.example.wyspring.vo.ocr.OcrQuestionOptionVO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 将 OCR 得到的纯文本按常见试卷版式拆成多道选择题（启发式，需用户在前端校对）。
 */
public final class OcrExamPaperParser {

    private static final Pattern QUESTION_HEAD = Pattern.compile(
            "^\\s*(?:第\\s*([0-9]+)\\s*题|[（(]?([0-9]+)[）)]?\\s*[\\.、．])\\s*(.*)$");
    private static final Pattern OPTION_LINE = Pattern.compile(
            "^\\s*([A-Da-d])[\\.、．\\)）]\\s*(.+)$");

    private OcrExamPaperParser() {
    }

    public static List<OcrQuestionItemVO> parse(String rawText) {
        List<OcrQuestionItemVO> list = new ArrayList<>();
        if (StringUtils.isBlank(rawText)) {
            return list;
        }
        String normalized = rawText.replace('\r', '\n').replace("　", " ");
        String[] rawLines = normalized.split("\n");
        List<String> lines = new ArrayList<>();
        for (String line : rawLines) {
            String t = line.trim();
            if (!t.isEmpty()) {
                lines.add(t);
            }
        }
        if (lines.isEmpty()) {
            return list;
        }

        List<List<String>> blocks = splitQuestionBlocks(lines);
        for (List<String> block : blocks) {
            OcrQuestionItemVO q = parseBlock(block);
            if (q != null && StringUtils.isNotBlank(q.getContent())) {
                list.add(q);
            }
        }

        if (list.isEmpty()) {
            list.add(buildFallbackQuestion(rawText));
        }
        return list;
    }

    /**
     * 按行首题号拆块；若无明显题号则整段为一块
     */
    private static List<List<String>> splitQuestionBlocks(List<String> lines) {
        List<List<String>> blocks = new ArrayList<>();
        List<String> current = new ArrayList<>();
        boolean seenAnyHead = false;

        for (String line : lines) {
            if (isQuestionHeadLine(line) && !current.isEmpty()) {
                seenAnyHead = true;
                blocks.add(new ArrayList<>(current));
                current.clear();
            }
            current.add(line);
        }
        if (!current.isEmpty()) {
            blocks.add(current);
        }

        if (!seenAnyHead && blocks.size() == 1 && blocks.get(0).size() > 8) {
            // 长文且无题号：尝试按 "数字+点" 再切
            return splitByInlineNumbers(blocks.get(0));
        }
        return blocks.isEmpty() ? Collections.singletonList(lines) : blocks;
    }

    private static boolean isQuestionHeadLine(String line) {
        if (line.startsWith("第") && line.contains("题")) {
            return true;
        }
        return line.matches("^\\s*[1-9]\\d{0,2}\\s*[\\.、．)].*");
    }

    private static List<List<String>> splitByInlineNumbers(List<String> lines) {
        List<List<String>> blocks = new ArrayList<>();
        List<String> cur = new ArrayList<>();
        Pattern p = Pattern.compile("^[1-9]\\d{0,2}[\\.、．]");
        for (String line : lines) {
            Matcher m = p.matcher(line);
            if (m.find() && !cur.isEmpty()) {
                blocks.add(new ArrayList<>(cur));
                cur.clear();
            }
            cur.add(line);
        }
        if (!cur.isEmpty()) {
            blocks.add(cur);
        }
        return blocks.isEmpty() ? List.of(lines) : blocks;
    }

    private static OcrQuestionItemVO parseBlock(List<String> block) {
        if (block.isEmpty()) {
            return null;
        }
        List<String> stemLines = new ArrayList<>();
        List<OcrQuestionOptionVO> options = new ArrayList<>();
        boolean inOptions = false;

        for (String line : block) {
            Matcher om = OPTION_LINE.matcher(line);
            if (om.matches()) {
                inOptions = true;
                String letter = om.group(1).toUpperCase();
                String text = om.group(2).trim();
                options.add(new OcrQuestionOptionVO(letter, text));
            } else if (!inOptions) {
                String cleaned = stripQuestionPrefix(line);
                if (StringUtils.isNotBlank(cleaned)) {
                    stemLines.add(cleaned);
                }
            }
            // 题干预备行之后若仍无选项，继续累积到题干（处理跨行题干）
        }

        String stem = String.join("\n", stemLines).trim();
        if (stem.isEmpty()) {
            stem = String.join("\n", block);
        }

        double confidence = options.size() >= 2 ? 0.82 : 0.55;

        if (options.size() < 2) {
            options.clear();
            options.add(new OcrQuestionOptionVO("A", "正确"));
            options.add(new OcrQuestionOptionVO("B", "错误"));
            confidence = 0.5;
        }

        OcrQuestionItemVO vo = new OcrQuestionItemVO();
        vo.setContent(stem);
        vo.setOptions(options);
        vo.setCorrectAnswer(0);
        vo.setConfidence(confidence);
        return vo;
    }

    private static String stripQuestionPrefix(String line) {
        Matcher m = QUESTION_HEAD.matcher(line);
        if (m.matches()) {
            String rest = m.group(3);
            return rest != null ? rest.trim() : line;
        }
        return line;
    }

    private static OcrQuestionItemVO buildFallbackQuestion(String rawText) {
        String shortText = rawText.length() > 800 ? rawText.substring(0, 800) + "…" : rawText;
        OcrQuestionItemVO vo = new OcrQuestionItemVO();
        vo.setContent(shortText.trim());
        List<OcrQuestionOptionVO> options = new ArrayList<>();
        options.add(new OcrQuestionOptionVO("A", "正确"));
        options.add(new OcrQuestionOptionVO("B", "错误"));
        vo.setOptions(options);
        vo.setCorrectAnswer(0);
        vo.setConfidence(0.45);
        return vo;
    }
}
