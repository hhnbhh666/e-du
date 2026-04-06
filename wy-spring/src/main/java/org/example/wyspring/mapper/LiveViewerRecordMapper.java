package org.example.wyspring.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.wyspring.entity.LiveViewerRecord;

import java.time.LocalDateTime;

/**
 * 直播观看记录Mapper
 */
@Mapper
public interface LiveViewerRecordMapper extends BaseMapper<LiveViewerRecord> {

    @Select("SELECT * FROM live_viewer_records WHERE room_id = #{roomId} AND user_id = #{userId} AND is_deleted = 0 ORDER BY created_at DESC LIMIT 1")
    LiveViewerRecord selectActiveRecord(@Param("roomId") Long roomId, @Param("userId") Long userId);

    @Update("UPDATE live_viewer_records SET leave_time = #{leaveTime}, watch_duration = TIMESTAMPDIFF(SECOND, enter_time, #{leaveTime}) WHERE id = #{recordId}")
    void updateLeaveTime(@Param("recordId") Long recordId, @Param("leaveTime") LocalDateTime leaveTime);

    @Select("SELECT COUNT(*) FROM live_viewer_records WHERE room_id = #{roomId} AND is_deleted = 0")
    Long countByRoomId(@Param("roomId") Long roomId);
}