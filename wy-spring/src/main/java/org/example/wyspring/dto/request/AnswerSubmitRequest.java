package org.example.wyspring.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 答题提交请求
 */
@Data
public class AnswerSubmitRequest {

    @NotNull(message = "题目ID不能为空")
    private Long questionId;

    @NotNull(message = "选择的选项不能为空")
    private Integer selectedOption;
}
