package org.example.wyspring.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.wyspring.entity.LiveRoom;

import java.util.List;

/**
 * 直播间Mapper
 */
@Mapper
public interface LiveRoomMapper extends BaseMapper<LiveRoom> {

    @Select("SELECT * FROM live_rooms WHERE status = 1 AND is_deleted = 0 ORDER BY viewer_count DESC LIMIT #{limit}")
    List<LiveRoom> selectHotRooms(@Param("limit") int limit);

    @Select("SELECT * FROM live_rooms WHERE status = 1 AND is_deleted = 0 ORDER BY created_at DESC LIMIT #{limit}")
    List<LiveRoom> selectLatestRooms(@Param("limit") int limit);

    @Select("SELECT * FROM live_rooms WHERE anchor_id = #{anchorId} AND status = 1 AND is_deleted = 0 LIMIT 1")
    LiveRoom selectLivingRoomByAnchor(@Param("anchorId") Long anchorId);

    @Select("SELECT * FROM live_rooms WHERE anchor_id = #{anchorId} AND status != 2 AND is_deleted = 0 LIMIT 1")
    LiveRoom selectActiveRoomByAnchor(@Param("anchorId") Long anchorId);

    @Update("UPDATE live_rooms SET viewer_count = viewer_count + 1 WHERE id = #{roomId}")
    void incrementViewerCount(@Param("roomId") Long roomId);

    @Update("UPDATE live_rooms SET viewer_count = viewer_count - 1 WHERE id = #{roomId} AND viewer_count > 0")
    void decrementViewerCount(@Param("roomId") Long roomId);

    @Update("UPDATE live_rooms SET like_count = like_count + 1 WHERE id = #{roomId}")
    void incrementLikeCount(@Param("roomId") Long roomId);

    @Update("UPDATE live_rooms SET total_watch_duration = total_watch_duration + #{duration} WHERE id = #{roomId}")
    void addWatchDuration(@Param("roomId") Long roomId, @Param("duration") Long duration);
}