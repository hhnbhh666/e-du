package org.example.wyspring.dto.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 发送弹幕请求
 */
@Data
public class DanmakuSendRequest {

    @NotBlank(message = "弹幕内容不能为空")
    @Size(max = 100, message = "弹幕最多100字符")
    private String content;

    private String color = "#FFFFFF";

    @Min(value = 1, message = "弹幕类型不正确")
    @Max(value = 3, message = "弹幕类型不正确")
    private Integer type = 1;
}