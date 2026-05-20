package com.ptit.clone.entity;

import com.ptit.clone.model.ProductStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "product")
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity {
    //x
    @Column(nullable = false, unique = true)
    private String slug;   //Đường dẫn URL sản phẩm phía người dùng
    //x
    @Column(nullable = false)
    private String name;  //Tên sản phẩm
    //x
    @Column(length = 1000)
    private String shortDescription; //Mô tả ngắn dùng card sản phẩm
    //x
    @Column(length = 12000)
    private String descriptionHtml; //Nội dung chi tiết HTML
    //x
    @Column(length = 1000)
    private String heroImage; //Ảnh đại diện chính sản phẩm
    //x
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status; //Trạng thái sản phẩm
    //x
    private LocalDateTime publishedAt; //Thời gian sản phẩm được publish

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;  //Thương hiệu
    //x
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_type_id", nullable = false)
    private ProductType productType;  //Loại sản phẩm
    //x
    @ManyToMany
    @JoinTable(
            name = "product_collection",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "collection_id")
    )
    private Set<ProductCollection> collections = new LinkedHashSet<>();  //Nằm trong bộ sưu tập nào

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductVariant> variants = new LinkedHashSet<>();  //Biến thể của sản phẩm
    //x
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductAttributeValue> attributes = new LinkedHashSet<>();  //Cấu hình, thuộc tính sản phâẩm

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ProductStats stats;
}

//x la nhung cai da them o buoc create product
