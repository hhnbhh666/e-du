package org.example.wyspring.service;

import org.example.wyspring.dto.request.DanmakuSendRequest;
import org.example.wyspring.dto.request.LiveRoomCreateRequest;
import org.example.wyspring.dto.request.LiveRoomUpdateRequest;
import org.example.wyspring.entity.LiveDanmaku;
import org.example.wyspring.entity.LiveRoom;
import org.example.wyspring.vo.DanmakuVO;
import org.example.wyspring.vo.LiveRoomStatsVO;
import org.example.wyspring.vo.LiveRoomVO;

import java.util.List;

/**
 * 直播服务接口
 */
public interface LiveService {

    LiveRoomVO createRoom(Long anchorId, LiveRoomCreateRequest request);

    LiveRoomVO updateRoom(Long anchorId, LiveRoomUpdateRequest request);

    LiveRoomVO startLive(Long anchorId);

    LiveRoomVO stopLive(Long anchorId);

    void deleteRoom(Long anchorId, Long roomId);

    LiveRoomVO getRoomDetail(Long roomId);

    LiveRoomVO getRoomByAnchor(Long anchorId);

    List<LiveRoomVO> getHotRooms(int limit);

    List<LiveRoomVO> getLatestRooms(int limit);

    List<LiveRoomVO> searchRooms(String keyword, int page, int size);

    void enterRoom(Long userId, Long roomId);

    void leaveRoom(Long userId, Long roomId);

    void likeRoom(Long userId, Long roomId);

    DanmakuVO sendDanmaku(Long userId, Long roomId, DanmakuSendRequest request);

    List<DanmakuVO> getRecentDanmaku(Long roomId, int limit);

    LiveRoomStatsVO getRoomStats(Long roomId);

    List<LiveRoomVO> getLiveRoomsByCategory(Integer categoryId, int page, int size);
}