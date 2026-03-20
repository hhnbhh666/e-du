package org.example.wyspring.vo.ocr;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OcrQuestionItemVO {
    private String content;
    private List<OcrQuestionOptionVO> options = new ArrayList<>();
    /** 正确选项下标，从 0 开始 */
    private Integer correctAnswer;
    /** 0~1，启发式置信度 */
    private Double confidence;
}
