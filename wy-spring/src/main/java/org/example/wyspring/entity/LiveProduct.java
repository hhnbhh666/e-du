package org.example.wyspring.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 直播商品实体（挂载小黄车）
 */
@Data
@TableName("live_products")
public class LiveProduct {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 直播间ID
     */
    private Long roomId;

    /**
     * 主播ID
     */
    private Long anchorId;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品图片
     */
    private String image;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商品链接
     */
    private String link;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 直播价格
     */
    private BigDecimal price;

    /**
     * 折扣比例（如 0.8 表示8折）
     */
    private BigDecimal discount;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * 已售数量
     */
    private Integer soldCount;

    /**
     * 排序（越小越靠前）
     */
    private Integer sortOrder;

    /**
     * 状态：0下架 1上架 2售罄
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 软删除
     */
    @TableLogic
    private Integer isDeleted;
}