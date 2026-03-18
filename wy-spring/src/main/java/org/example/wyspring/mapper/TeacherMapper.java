package org.example.wyspring.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.wyspring.entity.Teacher;

/**
 * 教师Mapper
 */
@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {

    /**
     * 根据用户ID查询教师
     */
    @Select("SELECT * FROM teachers WHERE user_id = #{userId} AND is_deleted = 0")
    Teacher selectByUserId(@Param("userId") Long userId);

    /**
     * 检查用户是否已申请教师
     */
    @Select("SELECT COUNT(*) FROM teachers WHERE user_id = #{userId} AND is_deleted = 0")
    int countByUserId(@Param("userId") Long userId);
}
