package org.example.wyspring.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.wyspring.entity.LiveCategory;

import java.util.List;

/**
 * 直播分类Mapper
 */
@Mapper
public interface LiveCategoryMapper extends BaseMapper<LiveCategory> {

    List<LiveCategory> selectEnabledCategories();
}