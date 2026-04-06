package org.example.wyspring.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 更新直播间请求
 */
@Data
public class LiveRoomUpdateRequest {

    @NotNull(message = "直播间ID不能为空")
    private Long id;

    @Size(max = 100, message = "标题最多100字符")
    private String title;

    @Size(max = 500, message = "封面图URL最多500字符")
    private String coverImage;

    @Size(max = 500, message = "描述最多500字符")
    private String description;

    private Integer categoryId;
}