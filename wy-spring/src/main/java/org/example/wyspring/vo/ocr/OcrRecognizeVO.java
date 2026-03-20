package org.example.wyspring.vo.ocr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OcrRecognizeVO {
    /** OCR 原始合并文本，便于校对 */
    private String rawText;
    private List<OcrQuestionItemVO> questions = new ArrayList<>();
}
