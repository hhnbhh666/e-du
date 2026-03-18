package org.example.wyspring.dto.request;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 创建题目请求
 */
@Data
public class QuestionCreateRequest {

    @NotBlank(message = "题目内容不能为空")
    private String content;

    private String imageUrl;

    private String videoUrl;

    @NotNull(message = "题型不能为空")
    private Integer type;

    @NotNull(message = "分类不能为空")
    private Integer categoryId;

    @NotNull(message = "难度不能为空")
    private Integer difficulty;

    @NotEmpty(message = "选项不能为空")
    @Valid
    private List<OptionRequest> options;

    @NotNull(message = "正确答案不能为空")
    private Integer correctAnswer;

    private String tip;

    private String explanation;

    @Data
    public static class OptionRequest {
        @NotBlank(message = "选项字母不能为空")
        private String letter;

        @NotBlank(message = "选项内容不能为空")
        private String content;
    }
}
