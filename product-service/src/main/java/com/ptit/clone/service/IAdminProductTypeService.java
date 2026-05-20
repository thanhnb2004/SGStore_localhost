package com.ptit.clone.service;

import com.ptit.clone.dtos.request.CreateProductTypeRequest;
import com.ptit.clone.dtos.response.ProductTypeListResponse;

import java.util.List;
import java.util.UUID;

public interface IAdminProductTypeService {
    void createProductType(CreateProductTypeRequest request);
    ProductTypeListResponse listProductTypes();
    void deleteProductTypes(List<UUID> productTypeIds);
}
