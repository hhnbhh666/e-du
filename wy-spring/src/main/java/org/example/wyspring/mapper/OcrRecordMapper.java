package org.example.wyspring.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.wyspring.entity.OcrRecord;

import java.util.List;

/**
 * OCR识别记录Mapper
 */
@Mapper
public interface OcrRecordMapper extends BaseMapper<OcrRecord> {

    /**
     * 分页查询用户的OCR记录
     */
    @Select("SELECT * FROM ocr_records WHERE user_id = #{userId} ORDER BY created_at DESC")
    IPage<OcrRecord> selectPageByUserId(Page<OcrRecord> page, @Param("userId") Long userId);

    /**
     * 查询老师的OCR记录
     */
    @Select("SELECT * FROM ocr_records WHERE teacher_id = #{teacherId} ORDER BY created_at DESC")
    List<OcrRecord> selectByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * 更新OCR处理状态
     */
    @Update("UPDATE ocr_records SET status = #{status}, ocr_text = #{ocrText}, " +
            "parsed_questions = #{parsedQuestions}, completed_at = NOW(), " +
            "error_message = #{errorMessage} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id,
                    @Param("status") Integer status,
                    @Param("ocrText") String ocrText,
                    @Param("parsedQuestions") String parsedQuestions,
                    @Param("errorMessage") String errorMessage);
}
