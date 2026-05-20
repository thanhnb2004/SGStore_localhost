package com.ptit.clone.service;

import com.ptit.clone.dtos.request.CreateProductRequest;
import com.ptit.clone.dtos.request.PublishProductRequest;
import com.ptit.clone.dtos.request.UpdateProductRequest;
import com.ptit.clone.dtos.response.ProductDetailResponse;
import com.ptit.clone.dtos.response.ProductListResponse;

import java.util.UUID;

public interface IAdminProductService {

    void createProduct(CreateProductRequest request);

    ProductListResponse getAllProducts();

    ProductDetailResponse getProduct(UUID productId);

    void updateProduct(UUID productId, UpdateProductRequest request);

    void archiveProduct(UUID productId);

    void deleteProduct(UUID productId);

    void publishProduct(UUID productId, PublishProductRequest request);
}
