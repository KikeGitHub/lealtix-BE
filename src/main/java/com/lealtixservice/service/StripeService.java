package com.lealtixservice.service;

import com.lealtixservice.dto.PagoDto;
import com.stripe.model.Product;
import com.stripe.model.Price;
import com.lealtixservice.dto.ProductPriceRequest;
import java.util.Map;

public interface StripeService {
    Product createProductWithPrice(ProductPriceRequest request) throws Exception;
    Price getLastCreatedPrice();
    Map<String, Object> createCheckoutSession(String priceId, String tenantId) throws Exception;

    PagoDto getCheckoutSession(String sessionId);

    PagoDto getCheckoutSessionCancel(String sessionId);
}
