package org.example.wyspring.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.wyspring.entity.Category;

import java.util.List;

/**
 * 分类Mapper
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 根据类型查询分类列表
     */
    @Select("SELECT * FROM categories WHERE type = #{type} AND status = 1 ORDER BY sort_order")
    List<Category> selectByType(@Param("type") Integer type);

    /**
     * 查询指定类型的顶级分类
     */
    @Select("SELECT * FROM categories WHERE type = #{type} AND parent_id = 0 AND status = 1 ORDER BY sort_order")
    List<Category> selectTopLevelByType(@Param("type") Integer type);
}
