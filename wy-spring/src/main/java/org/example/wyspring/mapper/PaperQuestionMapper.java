package org.example.wyspring.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.wyspring.entity.PaperQuestion;

import java.util.List;

/**
 * 试卷题目关联Mapper
 */
@Mapper
public interface PaperQuestionMapper extends BaseMapper<PaperQuestion> {

    /**
     * 查询试卷的题目关联列表
     */
    @Select("SELECT * FROM paper_questions WHERE paper_id = #{paperId} ORDER BY sort_order")
    List<PaperQuestion> selectByPaperId(@Param("paperId") Long paperId);

    /**
     * 查询试卷的题目ID列表
     */
    @Select("SELECT question_id FROM paper_questions WHERE paper_id = #{paperId} ORDER BY sort_order")
    List<Long> selectQuestionIdsByPaperId(@Param("paperId") Long paperId);

    /**
     * 检查题目是否已在试卷中
     */
    @Select("SELECT COUNT(*) FROM paper_questions WHERE paper_id = #{paperId} AND question_id = #{questionId}")
    int countByPaperAndQuestion(@Param("paperId") Long paperId, @Param("questionId") Long questionId);

    /**
     * 删除试卷的所有题目关联
     */
    @Select("DELETE FROM paper_questions WHERE paper_id = #{paperId}")
    int deleteByPaperId(@Param("paperId") Long paperId);
}
