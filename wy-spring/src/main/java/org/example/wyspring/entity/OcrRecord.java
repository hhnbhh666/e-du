package org.example.wyspring.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * OCR识别记录实体
 */
@Data
@TableName("ocr_records")
public class OcrRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 老师ID
     */
    private Long teacherId;

    /**
     * 原始上传图片URL
     */
    private String originalImage;

    /**
     * OCR识别的原始文本
     */
    private String ocrText;

    /**
     * 解析后的题目JSON
     */
    private String parsedQuestions;

    /**
     * 状态：0处理中 1成功 2失败
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 完成时间
     */
    private LocalDateTime completedAt;
}
