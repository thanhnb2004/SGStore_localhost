package com.ptit.clone.entity;

import com.ptit.clone.model.EntityStatus;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "catalog_collection")
public class ProductCollection extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String name;

    @Column(length = 5000)
    private String descriptionHtml;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntityStatus status;

//    dùng để quy định thứ tự hiển thị của collection (category/bộ sưu tập) trên UI hoặc khi query
   @Column(nullable = false)
    private Integer sortOrder;

    @Column(name = "hero_banner")
    private String heroBanner;

    @ElementCollection
    @CollectionTable(name = "collection_facet_code", joinColumns = @JoinColumn(name = "collection_id"))
    @Column(name = "facet_code")
    private List<String> facetCodes = new ArrayList<>();
}
