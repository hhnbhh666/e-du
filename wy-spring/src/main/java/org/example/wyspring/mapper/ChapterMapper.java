package org.example.wyspring.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.wyspring.entity.Chapter;

import java.util.List;

/**
 * 课程章节Mapper
 */
@Mapper
public interface ChapterMapper extends BaseMapper<Chapter> {

    /**
     * 查询课程的章节列表
     */
    @Select("SELECT * FROM chapters WHERE course_id = #{courseId} AND status = 1 ORDER BY sort_order")
    List<Chapter> selectByCourseId(@Param("courseId") Long courseId);
}
