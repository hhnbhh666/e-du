package org.example.wyspring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.wyspring.dto.request.DanmakuSendRequest;
import org.example.wyspring.dto.request.LiveRoomCreateRequest;
import org.example.wyspring.dto.request.LiveRoomUpdateRequest;
import org.example.wyspring.dto.response.PageResult;
import org.example.wyspring.dto.response.Result;
import org.example.wyspring.service.LiveService;
import org.example.wyspring.utils.CurrentUserUtils;
import org.example.wyspring.vo.DanmakuVO;
import org.example.wyspring.vo.LiveRoomStatsVO;
import org.example.wyspring.vo.LiveRoomVO;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 直播Controller
 */
@RestController
@RequestMapping("/api/live")
@RequiredArgsConstructor
@Tag(name = "直播接口")
public class LiveController {

    private final LiveService liveService;

    @PostMapping("/room/create")
    @Operation(summary = "创建直播间")
    public Result<LiveRoomVO> createRoom(@RequestBody @Valid LiveRoomCreateRequest request) {
        Long anchorId = CurrentUserUtils.getCurrentUserId();
        LiveRoomVO room = liveService.createRoom(anchorId, request);
        return Result.success(room);
    }

    @PutMapping("/room/update")
    @Operation(summary = "更新直播间信息")
    public Result<LiveRoomVO> updateRoom(@RequestBody @Valid LiveRoomUpdateRequest request) {
        Long anchorId = CurrentUserUtils.getCurrentUserId();
        LiveRoomVO room = liveService.updateRoom(anchorId, request);
        return Result.success(room);
    }

    @PostMapping("/room/start")
    @Operation(summary = "开始直播")
    public Result<LiveRoomVO> startLive() {
        Long anchorId = CurrentUserUtils.getCurrentUserId();
        LiveRoomVO room = liveService.startLive(anchorId);
        return Result.success(room);
    }

    @PostMapping("/room/stop")
    @Operation(summary = "结束直播")
    public Result<LiveRoomVO> stopLive() {
        Long anchorId = CurrentUserUtils.getCurrentUserId();
        LiveRoomVO room = liveService.stopLive(anchorId);
        return Result.success(room);
    }

    @DeleteMapping("/room/{roomId}")
    @Operation(summary = "删除直播间")
    public Result<Void> deleteRoom(@PathVariable Long roomId) {
        Long anchorId = CurrentUserUtils.getCurrentUserId();
        liveService.deleteRoom(anchorId, roomId);
        return Result.success(null);
    }

    @GetMapping("/room/{roomId}")
    @Operation(summary = "获取直播间详情")
    public Result<LiveRoomVO> getRoomDetail(@PathVariable Long roomId) {
        LiveRoomVO room = liveService.getRoomDetail(roomId);
        return Result.success(room);
    }

    @GetMapping("/room/anchor/current")
    @Operation(summary = "获取当前用户的直播间")
    public Result<LiveRoomVO> getMyRoom() {
        Long anchorId = CurrentUserUtils.getCurrentUserId();
        LiveRoomVO room = liveService.getRoomByAnchor(anchorId);
        return Result.success(room);
    }

    @GetMapping("/rooms/hot")
    @Operation(summary = "获取热门直播间")
    public Result<List<LiveRoomVO>> getHotRooms(
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "10") int limit) {
        List<LiveRoomVO> rooms = liveService.getHotRooms(limit);
        return Result.success(rooms);
    }

    @GetMapping("/rooms/latest")
    @Operation(summary = "获取最新直播间")
    public Result<List<LiveRoomVO>> getLatestRooms(
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "10") int limit) {
        List<LiveRoomVO> rooms = liveService.getLatestRooms(limit);
        return Result.success(rooms);
    }

    @GetMapping("/rooms/category/{categoryId}")
    @Operation(summary = "获取指定分类的直播间")
    public Result<PageResult<LiveRoomVO>> getRoomsByCategory(
            @PathVariable Integer categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<LiveRoomVO> rooms = liveService.getLiveRoomsByCategory(categoryId, page, size);
        return Result.success(new PageResult<>(rooms));
    }

    @PostMapping("/room/{roomId}/enter")
    @Operation(summary = "进入直播间")
    public Result<Void> enterRoom(@PathVariable Long roomId) {
        Long userId = CurrentUserUtils.getCurrentUserId();
        liveService.enterRoom(userId, roomId);
        return Result.success(null);
    }

    @PostMapping("/room/{roomId}/leave")
    @Operation(summary = "离开直播间")
    public Result<Void> leaveRoom(@PathVariable Long roomId) {
        Long userId = CurrentUserUtils.getCurrentUserId();
        liveService.leaveRoom(userId, roomId);
        return Result.success(null);
    }

    @PostMapping("/room/{roomId}/like")
    @Operation(summary = "点赞直播间")
    public Result<Void> likeRoom(@PathVariable Long roomId) {
        Long userId = CurrentUserUtils.getCurrentUserId();
        liveService.likeRoom(userId, roomId);
        return Result.success(null);
    }

    @PostMapping("/room/{roomId}/danmaku")
    @Operation(summary = "发送弹幕")
    public Result<DanmakuVO> sendDanmaku(
            @PathVariable Long roomId,
            @RequestBody @Valid DanmakuSendRequest request) {
        Long userId = CurrentUserUtils.getCurrentUserId();
        DanmakuVO danmaku = liveService.sendDanmaku(userId, roomId, request);
        return Result.success(danmaku);
    }

    @GetMapping("/room/{roomId}/danmaku")
    @Operation(summary = "获取最近弹幕")
    public Result<List<DanmakuVO>> getRecentDanmaku(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "50") int limit) {
        List<DanmakuVO> danmakus = liveService.getRecentDanmaku(roomId, limit);
        return Result.success(danmakus);
    }

    @GetMapping("/room/{roomId}/stats")
    @Operation(summary = "获取直播间统计")
    public Result<LiveRoomStatsVO> getRoomStats(@PathVariable Long roomId) {
        LiveRoomStatsVO stats = liveService.getRoomStats(roomId);
        return Result.success(stats);
    }
}