package org.example.wyspring.vo;

import lombok.Data;

import java.util.List;

/**
 * 题目视图对象
 */
@Data
public class QuestionVO {

    private Long id;

    private String content;

    private String imageUrl;

    private Integer type;

    private Integer difficulty;

    private Integer categoryId;

    /**
     * 选项列表
     */
    private List<OptionVO> options;

    /**
     * 用户是否已答过
     */
    private Boolean hasAnswered;

    /**
     * 用户是否答对
     */
    private Boolean correct;

    /**
     * 是否正确答案（仅在提交答案后显示）
     */
    private Integer correctAnswer;

    /**
     * 答题技巧/口诀（仅在提交答案后显示）
     */
    private String tip;

    /**
     * 详细解析（仅在提交答案后显示）
     */
    private String explanation;

    /**
     * 答题统计
     */
    private Integer answerCount;

    private Integer correctCount;

    /**
     * 收藏状态
     */
    private Boolean isFavorited;

    @Data
    public static class OptionVO {
        private String letter;
        private String content;
    }
}
