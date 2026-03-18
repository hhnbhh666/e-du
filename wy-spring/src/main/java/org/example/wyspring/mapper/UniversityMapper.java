package org.example.wyspring.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.wyspring.entity.University;

import java.util.List;

/**
 * 大学Mapper
 */
@Mapper
public interface UniversityMapper extends BaseMapper<University> {

    /**
     * 查询启用的大学列表
     */
    @Select("SELECT * FROM universities WHERE status = 1 ORDER BY sort_order")
    List<University> selectEnabledList();
}
