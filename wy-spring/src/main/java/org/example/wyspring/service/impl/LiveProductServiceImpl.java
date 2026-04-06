package org.example.wyspring.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.wyspring.dto.request.LiveProductAddRequest;
import org.example.wyspring.entity.LiveProduct;
import org.example.wyspring.entity.LiveRoom;
import org.example.wyspring.enums.LiveErrorCode;
import org.example.wyspring.enums.LiveProductStatus;
import org.example.wyspring.exception.BusinessException;
import org.example.wyspring.mapper.LiveProductMapper;
import org.example.wyspring.mapper.LiveRoomMapper;
import org.example.wyspring.service.LiveProductService;
import org.example.wyspring.vo.LiveProductVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 直播商品服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LiveProductServiceImpl implements LiveProductService {

    private final LiveProductMapper productMapper;
    private final LiveRoomMapper roomMapper;

    @Override
    @Transactional
    public LiveProductVO addProduct(Long anchorId, LiveProductAddRequest request) {
        LiveRoom room = roomMapper.selectById(request.getRoomId());
        if (room == null || room.getIsDeleted() == 1) {
            throw new BusinessException(LiveErrorCode.LIVE_ROOM_NOT_FOUND);
        }
        if (!room.getAnchorId().equals(anchorId)) {
            throw new BusinessException(LiveErrorCode.LIVE_NOT_PERMISSION);
        }

        LiveProduct product = new LiveProduct();
        product.setRoomId(request.getRoomId());
        product.setAnchorId(anchorId);
        product.setName(request.getName());
        product.setImage(request.getImage());
        product.setDescription(request.getDescription());
        product.setLink(request.getLink());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setPrice(request.getPrice());
        product.setDiscount(request.getDiscount());
        product.setStock(request.getStock());
        product.setSoldCount(0);
        product.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        product.setStatus(LiveProductStatus.ONLINE.getCode());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        productMapper.insert(product);

        room.setProductCount(room.getProductCount() != null ? room.getProductCount() + 1 : 1);
        roomMapper.updateById(room);

        log.info("[LIVE_PRODUCT_ADD] anchorId={}, roomId={}, productId={}", anchorId, request.getRoomId(), product.getId());
        return convertToVO(product);
    }

    @Override
    @Transactional
    public LiveProductVO updateProduct(Long anchorId, Long productId, LiveProductAddRequest request) {
        LiveProduct product = productMapper.selectById(productId);
        if (product == null || product.getIsDeleted() == 1) {
            throw new BusinessException(LiveErrorCode.LIVE_PRODUCT_NOT_FOUND);
        }
        if (!product.getAnchorId().equals(anchorId)) {
            throw new BusinessException(LiveErrorCode.LIVE_NOT_PERMISSION);
        }

        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getImage() != null) {
            product.setImage(request.getImage());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getLink() != null) {
            product.setLink(request.getLink());
        }
        if (request.getOriginalPrice() != null) {
            product.setOriginalPrice(request.getOriginalPrice());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getDiscount() != null) {
            product.setDiscount(request.getDiscount());
        }
        if (request.getStock() != null) {
            product.setStock(request.getStock());
        }
        if (request.getSortOrder() != null) {
            product.setSortOrder(request.getSortOrder());
        }
        product.setUpdatedAt(LocalDateTime.now());

        productMapper.updateById(product);

        log.info("[LIVE_PRODUCT_UPDATE] anchorId={}, productId={}", anchorId, productId);
        return convertToVO(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long anchorId, Long productId) {
        LiveProduct product = productMapper.selectById(productId);
        if (product == null || product.getIsDeleted() == 1) {
            throw new BusinessException(LiveErrorCode.LIVE_PRODUCT_NOT_FOUND);
        }
        if (!product.getAnchorId().equals(anchorId)) {
            throw new BusinessException(LiveErrorCode.LIVE_NOT_PERMISSION);
        }

        productMapper.deleteById(productId);

        LiveRoom room = roomMapper.selectById(product.getRoomId());
        if (room != null && room.getProductCount() != null && room.getProductCount() > 0) {
            room.setProductCount(room.getProductCount() - 1);
            roomMapper.updateById(room);
        }

        log.info("[LIVE_PRODUCT_DELETE] anchorId={}, productId={}", anchorId, productId);
    }

    @Override
    @Transactional
    public void toggleProductStatus(Long anchorId, Long productId) {
        LiveProduct product = productMapper.selectById(productId);
        if (product == null || product.getIsDeleted() == 1) {
            throw new BusinessException(LiveErrorCode.LIVE_PRODUCT_NOT_FOUND);
        }
        if (!product.getAnchorId().equals(anchorId)) {
            throw new BusinessException(LiveErrorCode.LIVE_NOT_PERMISSION);
        }

        if (product.getStatus().equals(LiveProductStatus.ONLINE.getCode())) {
            product.setStatus(LiveProductStatus.OFFLINE.getCode());
        } else if (product.getStatus().equals(LiveProductStatus.OFFLINE.getCode())) {
            product.setStatus(LiveProductStatus.ONLINE.getCode());
        }
        product.setUpdatedAt(LocalDateTime.now());

        productMapper.updateById(product);

        log.info("[LIVE_PRODUCT_TOGGLE] anchorId={}, productId={}, newStatus={}", anchorId, productId, product.getStatus());
    }

    @Override
    public LiveProductVO getProductDetail(Long productId) {
        LiveProduct product = productMapper.selectById(productId);
        if (product == null || product.getIsDeleted() == 1) {
            throw new BusinessException(LiveErrorCode.LIVE_PRODUCT_NOT_FOUND);
        }
        return convertToVO(product);
    }

    @Override
    public List<LiveProductVO> getRoomProducts(Long roomId) {
        List<LiveProduct> products = productMapper.selectActiveProductsByRoom(roomId);
        return products.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<LiveProductVO> getAnchorProducts(Long anchorId) {
        List<LiveProduct> products = productMapper.selectByAnchor(anchorId);
        return products.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateProductStock(Long productId, int count) {
        LiveProduct product = productMapper.selectById(productId);
        if (product == null || product.getIsDeleted() == 1) {
            throw new BusinessException(LiveErrorCode.LIVE_PRODUCT_NOT_FOUND);
        }

        int newStock = product.getStock() - count;
        if (newStock < 0) {
            newStock = 0;
        }
        product.setStock(newStock);
        product.setSoldCount(product.getSoldCount() + count);

        if (newStock == 0) {
            product.setStatus(LiveProductStatus.SOLD_OUT.getCode());
        }
        product.setUpdatedAt(LocalDateTime.now());

        productMapper.updateById(product);

        log.info("[LIVE_PRODUCT_STOCK_UPDATE] productId={}, sold={}, remaining={}", productId, count, newStock);
    }

    private LiveProductVO convertToVO(LiveProduct product) {
        LiveProductVO vo = new LiveProductVO();
        vo.setId(product.getId());
        vo.setRoomId(product.getRoomId());
        vo.setAnchorId(product.getAnchorId());
        vo.setName(product.getName());
        vo.setImage(product.getImage());
        vo.setDescription(product.getDescription());
        vo.setLink(product.getLink());
        vo.setOriginalPrice(product.getOriginalPrice());
        vo.setPrice(product.getPrice());
        vo.setDiscount(product.getDiscount());
        vo.setStock(product.getStock());
        vo.setSoldCount(product.getSoldCount());
        vo.setSortOrder(product.getSortOrder());
        vo.setStatus(product.getStatus());
        vo.setStatusName(LiveProductStatus.fromCode(product.getStatus()).getName());
        vo.setCreatedAt(product.getCreatedAt());
        return vo;
    }
}