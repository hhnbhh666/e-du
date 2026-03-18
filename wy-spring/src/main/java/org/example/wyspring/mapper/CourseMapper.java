package org.example.wyspring.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.wyspring.entity.Course;

import java.util.List;

/**
 * 课程Mapper
 */
@Mapper
public interface CourseMapper extends BaseMapper<Course> {

    /**
     * 分页查询课程列表
     */
    @Select("SELECT * FROM courses WHERE status = 1 AND is_deleted = 0 " +
            "AND (#{categoryId} IS NULL OR category_id = #{categoryId}) " +
            "ORDER BY sort_order, created_at DESC")
    IPage<Course> selectPageList(Page<Course> page, @Param("categoryId") Integer categoryId);

    /**
     * 查询课程详情
     */
    @Select("SELECT * FROM courses WHERE id = #{id} AND is_deleted = 0")
    Course selectByIdWithDeleted(@Param("id") Long id);

    /**
     * 增加学员数量
     */
    @Update("UPDATE courses SET students_count = students_count + 1 WHERE id = #{id}")
    int incrementStudentsCount(@Param("id") Long id);

    /**
     * 查询推荐课程
     */
    @Select("SELECT * FROM courses WHERE status = 1 AND is_deleted = 0 ORDER BY students_count DESC LIMIT #{limit}")
    List<Course> selectHotCourses(@Param("limit") Integer limit);
}
