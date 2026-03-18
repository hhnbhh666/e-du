package org.example.wyspring.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.wyspring.entity.QuestionOption;

import java.util.List;

/**
 * 题目选项Mapper
 */
@Mapper
public interface QuestionOptionMapper extends BaseMapper<QuestionOption> {

    /**
     * 查询题目的选项列表
     */
    @Select("SELECT * FROM question_options WHERE question_id = #{questionId} ORDER BY sort_order")
    List<QuestionOption> selectByQuestionId(@Param("questionId") Long questionId);

    /**
     * 批量查询题目的选项
     */
    @Select("<script>" +
            "SELECT * FROM question_options WHERE question_id IN " +
            "<foreach collection='questionIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            " ORDER BY question_id, sort_order" +
            "</script>")
    List<QuestionOption> selectByQuestionIds(@Param("questionIds") List<Long> questionIds);
}
