package org.example.wyspring.dto.request;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * 添加直播商品请求
 */
@Data
public class LiveProductAddRequest {

    @NotNull(message = "直播间ID不能为空")
    private Long roomId;

    @NotBlank(message = "商品名称不能为空")
    @Size(max = 200, message = "商品名称最多200字符")
    private String name;

    @NotBlank(message = "商品图片不能为空")
    private String image;

    @Size(max = 500, message = "商品描述最多500字符")
    private String description;

    @NotBlank(message = "商品链接不能为空")
    private String link;

    @NotNull(message = "原价不能为空")
    @DecimalMin(value = "0.01", message = "原价必须大于0")
    private BigDecimal originalPrice;

    @NotNull(message = "直播价格不能为空")
    @DecimalMin(value = "0.01", message = "直播价格必须大于0")
    private BigDecimal price;

    @DecimalMin(value = "0.1", message = "折扣比例最小0.1")
    private BigDecimal discount;

    @NotNull(message = "库存不能为空")
    @Min(value = 1, message = "库存最小为1")
    private Integer stock;

    private Integer sortOrder = 0;
}