package org.example.wyspring.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.wyspring.entity.ExamPaper;

import java.util.List;

/**
 * 试卷Mapper
 */
@Mapper
public interface ExamPaperMapper extends BaseMapper<ExamPaper> {

    /**
     * 分页查询试卷列表
     */
    @Select("SELECT * FROM exam_papers WHERE status = #{status} AND is_deleted = 0 " +
            "AND (#{categoryId} IS NULL OR category_id = #{categoryId}) " +
            "ORDER BY created_at DESC")
    IPage<ExamPaper> selectPageList(Page<ExamPaper> page,
                                   @Param("status") Integer status,
                                   @Param("categoryId") Integer categoryId);

    /**
     * 查询老师的试卷列表
     */
    @Select("SELECT * FROM exam_papers WHERE teacher_id = #{teacherId} AND is_deleted = 0 " +
            "ORDER BY created_at DESC")
    List<ExamPaper> selectByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * 更新试卷状态
     */
    @Update("UPDATE exam_papers SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新总题数
     */
    @Update("UPDATE exam_papers SET total_questions = #{totalQuestions} WHERE id = #{id}")
    int updateTotalQuestions(@Param("id") Long id, @Param("totalQuestions") Integer totalQuestions);
}
