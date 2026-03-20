package org.example.wyspring.service.ocr;

import lombok.RequiredArgsConstructor;
import org.example.wyspring.enums.ErrorCode;
import org.example.wyspring.exception.BusinessException;
import org.example.wyspring.vo.ocr.OcrQuestionItemVO;
import org.example.wyspring.vo.ocr.OcrRecognizeVO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 试卷图片 OCR + 题目拆分
 */
@Service
@RequiredArgsConstructor
public class TeacherOcrService {

    private static final long MAX_BYTES = 10 * 1024 * 1024;

    private final BaiduOcrClient baiduOcrClient;

    public OcrRecognizeVO recognize(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "请上传图片文件");
        }
        if (file.getSize() > MAX_BYTES) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "图片不能超过 10MB");
        }
        try {
            byte[] bytes = file.getBytes();
            String rawText = baiduOcrClient.recognizeText(bytes);
            List<OcrQuestionItemVO> questions = OcrExamPaperParser.parse(rawText);
            return new OcrRecognizeVO(rawText, questions);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OCR_PROCESSING_ERROR, e.getMessage() != null ? e.getMessage() : "读取图片失败");
        }
    }
}
