package org.example.wyspring.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.wyspring.entity.LiveProduct;

import java.util.List;

/**
 * 直播商品Mapper
 */
@Mapper
public interface LiveProductMapper extends BaseMapper<LiveProduct> {

    @Select("SELECT * FROM live_products WHERE room_id = #{roomId} AND status = 1 AND is_deleted = 0 ORDER BY sort_order ASC")
    List<LiveProduct> selectActiveProductsByRoom(@Param("roomId") Long roomId);

    @Select("SELECT * FROM live_products WHERE anchor_id = #{anchorId} AND is_deleted = 0 ORDER BY created_at DESC")
    List<LiveProduct> selectByAnchor(@Param("anchorId") Long anchorId);

    @Update("UPDATE live_products SET sold_count = sold_count + #{count} WHERE id = #{productId}")
    void incrementSoldCount(@Param("productId") Long productId, @Param("count") int count);

    @Update("UPDATE live_products SET status = 2 WHERE id = #{productId} AND stock <= 0")
    void updateStatusToSoldOut(@Param("productId") Long productId);
}