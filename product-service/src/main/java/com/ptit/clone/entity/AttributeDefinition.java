package com.ptit.clone.entity;

import com.ptit.clone.model.AttributeScope;
import com.ptit.clone.model.AttributeValueType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "attribute_definition")
public class AttributeDefinition extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

//    Note
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttributeScope scope;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttributeValueType valueType;

    @Column(nullable = false)
    private Boolean facetable;

    @Column(nullable = false)
    private Integer filterPosition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_type_id")
    private ProductType productType;
}
