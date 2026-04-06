package org.example.wyspring.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 直播商品视图对象
 */
@Data
public class LiveProductVO {

    private Long id;

    private Long roomId;

    private Long anchorId;

    private String name;

    private String image;

    private String description;

    private String link;

    private BigDecimal originalPrice;

    private BigDecimal price;

    private BigDecimal discount;

    private Integer stock;

    private Integer soldCount;

    private Integer sortOrder;

    private Integer status;

    private String statusName;

    private LocalDateTime createdAt;
}