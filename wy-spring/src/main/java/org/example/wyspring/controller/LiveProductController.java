package org.example.wyspring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.wyspring.dto.request.LiveProductAddRequest;
import org.example.wyspring.dto.response.Result;
import org.example.wyspring.service.LiveProductService;
import org.example.wyspring.utils.CurrentUserUtils;
import org.example.wyspring.vo.LiveProductVO;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 直播商品Controller
 */
@RestController
@RequestMapping("/api/live/product")
@RequiredArgsConstructor
@Tag(name = "直播商品接口（小黄车）")
public class LiveProductController {

    private final LiveProductService productService;

    @PostMapping("/add")
    @Operation(summary = "添加直播商品")
    public Result<LiveProductVO> addProduct(@RequestBody @Valid LiveProductAddRequest request) {
        Long anchorId = CurrentUserUtils.getCurrentUserId();
        LiveProductVO product = productService.addProduct(anchorId, request);
        return Result.success(product);
    }

    @PutMapping("/update/{productId}")
    @Operation(summary = "更新直播商品")
    public Result<LiveProductVO> updateProduct(
            @PathVariable Long productId,
            @RequestBody @Valid LiveProductAddRequest request) {
        Long anchorId = CurrentUserUtils.getCurrentUserId();
        LiveProductVO product = productService.updateProduct(anchorId, productId, request);
        return Result.success(product);
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "删除直播商品")
    public Result<Void> deleteProduct(@PathVariable Long productId) {
        Long anchorId = CurrentUserUtils.getCurrentUserId();
        productService.deleteProduct(anchorId, productId);
        return Result.success(null);
    }

    @PostMapping("/{productId}/toggle")
    @Operation(summary = "切换商品上下架状态")
    public Result<Void> toggleProductStatus(@PathVariable Long productId) {
        Long anchorId = CurrentUserUtils.getCurrentUserId();
        productService.toggleProductStatus(anchorId, productId);
        return Result.success(null);
    }

    @GetMapping("/{productId}")
    @Operation(summary = "获取商品详情")
    public Result<LiveProductVO> getProductDetail(@PathVariable Long productId) {
        LiveProductVO product = productService.getProductDetail(productId);
        return Result.success(product);
    }

    @GetMapping("/room/{roomId}")
    @Operation(summary = "获取直播间的商品列表")
    public Result<List<LiveProductVO>> getRoomProducts(@PathVariable Long roomId) {
        List<LiveProductVO> products = productService.getRoomProducts(roomId);
        return Result.success(products);
    }

    @GetMapping("/anchor")
    @Operation(summary = "获取当前主播的商品列表")
    public Result<List<LiveProductVO>> getMyProducts() {
        Long anchorId = CurrentUserUtils.getCurrentUserId();
        List<LiveProductVO> products = productService.getAnchorProducts(anchorId);
        return Result.success(products);
    }
}