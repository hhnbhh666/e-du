package org.example.wyspring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.wyspring.dto.response.Result;
import org.example.wyspring.service.ocr.TeacherOcrService;
import org.example.wyspring.vo.ocr.OcrRecognizeVO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 老师端：试卷 OCR
 */
@RestController
@RequestMapping("/api/v1/teacher/ocr")
@RequiredArgsConstructor
@Tag(name = "老师端-OCR")
public class TeacherOcrController {

    private final TeacherOcrService teacherOcrService;

    @PostMapping("/recognize")
    @Operation(summary = "上传试卷图片，百度 OCR 识别并拆题")
    public Result<OcrRecognizeVO> recognize(@RequestPart("file") MultipartFile file) {
        return Result.success(teacherOcrService.recognize(file));
    }
}
