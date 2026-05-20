package com.ptit.clone.service;

import com.ptit.clone.dtos.request.CreateBrandRequest;
import com.ptit.clone.dtos.request.UpdateBrandRequest;
import com.ptit.clone.dtos.response.BrandResponse;
import com.ptit.clone.dtos.response.BrandListResponse;
import com.ptit.clone.model.EntityStatus;

import java.util.List;
import java.util.UUID;

public interface IAdminBrandService {
    void createBrand(CreateBrandRequest request);

    BrandResponse getBrand(UUID brandId);

    void updateBrand(UUID brandId, UpdateBrandRequest request);

    BrandListResponse listBrands(EntityStatus status);

    void deleteBrands(List<UUID> brandIds);
}
