package org.example.wyspring.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.wyspring.entity.Question;

import java.util.List;

/**
 * 题目Mapper
 */
@Mapper
public interface QuestionMapper extends BaseMapper<Question> {

    /**
     * 分页查询题目列表（基础字段）
     */
    @Select("SELECT id, content, image_url, type, difficulty, correct_answer, " +
            "answer_count, correct_count, status " +
            "FROM questions WHERE status = 1 AND is_deleted = 0 " +
            "AND (#{categoryId} IS NULL OR category_id = #{categoryId}) " +
            "AND (#{type} IS NULL OR type = #{type}) " +
            "AND (#{difficulty} IS NULL OR difficulty = #{difficulty}) " +
            "ORDER BY created_at DESC")
    IPage<Question> selectPageList(Page<Question> page,
                                   @Param("categoryId") Integer categoryId,
                                   @Param("type") Integer type,
                                   @Param("difficulty") Integer difficulty);

    /**
     * 查询题目详情（含解析）
     */
    @Select("SELECT * FROM questions WHERE id = #{id} AND is_deleted = 0")
    Question selectDetailById(@Param("id") Long id);

    /**
     * 查询老师的题目列表
     */
    @Select("SELECT * FROM questions WHERE teacher_id = #{teacherId} AND is_deleted = 0 " +
            "ORDER BY created_at DESC")
    List<Question> selectByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * 更新题目统计（答对/答错次数）
     */
    @Update("UPDATE questions SET answer_count = answer_count + 1, " +
            "correct_count = correct_count + #{isCorrect} WHERE id = #{questionId}")
    int updateStats(@Param("questionId") Long questionId, @Param("isCorrect") Integer isCorrect);
}
