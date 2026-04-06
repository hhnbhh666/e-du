package org.example.wyspring.service;

import org.example.wyspring.dto.request.LiveProductAddRequest;
import org.example.wyspring.vo.LiveProductVO;

import java.util.List;

/**
 * 直播商品服务接口
 */
public interface LiveProductService {

    LiveProductVO addProduct(Long anchorId, LiveProductAddRequest request);

    LiveProductVO updateProduct(Long anchorId, Long productId, LiveProductAddRequest request);

    void deleteProduct(Long anchorId, Long productId);

    void toggleProductStatus(Long anchorId, Long productId);

    LiveProductVO getProductDetail(Long productId);

    List<LiveProductVO> getRoomProducts(Long roomId);

    List<LiveProductVO> getAnchorProducts(Long anchorId);

    void updateProductStock(Long productId, int count);
}