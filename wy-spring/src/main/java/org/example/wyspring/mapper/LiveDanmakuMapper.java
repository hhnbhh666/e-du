package org.example.wyspring.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.wyspring.entity.LiveDanmaku;

import java.util.List;

/**
 * 直播弹幕Mapper
 */
@Mapper
public interface LiveDanmakuMapper extends BaseMapper<LiveDanmaku> {

    @Select("SELECT * FROM live_danmaku WHERE room_id = #{roomId} AND is_deleted = 0 ORDER BY created_at DESC LIMIT #{limit}")
    List<LiveDanmaku> selectRecentDanmaku(@Param("roomId") Long roomId, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM live_danmaku WHERE room_id = #{roomId} AND is_deleted = 0")
    Long countByRoomId(@Param("roomId") Long roomId);
}