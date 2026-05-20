package com.ptit.clone.service.Iplm;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.ChildScoreMode;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.ptit.clone.dtos.response.PageResponse;
import com.ptit.clone.dtos.response.ProductSummaryResponse;
import com.ptit.clone.dtos.response.ProductSummaryListResponse;
import com.ptit.clone.dtos.response.VariantSummaryResponse;
import com.ptit.clone.entity.ProductDocument;
import com.ptit.clone.mapper.ProductDocumentMapper;
import com.ptit.clone.service.IProductQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductQueryServiceIplm implements IProductQueryService {

    private final ElasticsearchOperations operations;
    private final ProductDocumentMapper mapper;

    @Override
    public ProductSummaryListResponse search(String query, Integer page, Integer size, String sortBy) {
        if (!operations.indexOps(ProductDocument.class).exists()) {
            return emptyResponse(page, size);
        }
        if (query == null || query.isBlank()) {
            return emptyResponse(page, size);
        }

        int safePage = page != null ? page : 1;
        int safeSize = size != null ? size : 20;
        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(fullTextQuery(query))
                .withSort(sortQuery(sortBy))
                .withPageable(PageRequest.of(safePage - 1, safeSize))
                .withTrackTotalHits(true)
                .build();

        List<ProductSummaryResponse> items = new ArrayList<>();
        SearchHits<ProductDocument> hits = operations.search(searchQuery, ProductDocument.class);
        List<SearchHit<ProductDocument>> searchHits = hits.getSearchHits();
        for (SearchHit<ProductDocument> hit : searchHits) {
            ProductSummaryResponse item = mapper.toSearchProductItemResponse(hit.getContent());
            item.setScore(hit.getScore());
            item.setDiscountPercent(discount(item.getVariant()));
            items.add(item);
        }

        long totalItems = hits.getTotalHits();
        int totalPages = 0;

        if (totalItems != 0) {
            totalPages = (int) Math.ceil((double) totalItems / safeSize);
        }
        PageResponse pageResponse = PageResponse.builder()
                .page(safePage)
                .size(safeSize)
                .totalItems(totalItems)
                .totalPages(totalPages)
                .build();

        return ProductSummaryListResponse.builder()
                .items(items)
                .pagination(pageResponse)
                .build();
    }

    private ProductSummaryListResponse emptyResponse(Integer page, Integer size) {
        int safePage = page != null ? page : 1;
        int safeSize = size != null ? size : 20;
        PageResponse pageResponse = PageResponse.builder()
                .page(page)
                .size(size)
                .totalItems(0L)
                .totalPages(0)
                .build();

        return ProductSummaryListResponse.builder()
                .items(List.of())
                .pagination(pageResponse)
                .build();
    }

    @Override
    public List<ProductSummaryResponse> getBestSellers(String category, Integer limit) {
        if (!operations.indexOps(ProductDocument.class).exists()) {
            return List.of();
        }

        int safeLimit = (limit != null && limit > 0) ? limit : 10;

        Query filterQuery;
        if (category != null && !category.isBlank()) {
            filterQuery = QueryBuilders.term(t -> t
                    .field("product_type.product_type_slug")
                    .value(category)
            );
        } else {
            filterQuery = QueryBuilders.matchAll(ma -> ma);
        }

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(filterQuery)
                .withSort(SortOptions.of(s -> s.field(f -> f.field("product_stats.sold_count").order(SortOrder.Desc))))
                .withPageable(PageRequest.of(0, safeLimit))
                .build();

        List<ProductSummaryResponse> items = new ArrayList<>();
        SearchHits<ProductDocument> hits = operations.search(nativeQuery, ProductDocument.class);
        for (SearchHit<ProductDocument> hit : hits.getSearchHits()) {
            ProductSummaryResponse item = mapper.toSearchProductItemResponse(hit.getContent());
            item.setDiscountPercent(discount(item.getVariant()));
            items.add(item);
        }
        return items;
    }

    private Query fullTextQuery(String query) {
        return QueryBuilders.bool(b -> b
                .should(s -> s.term(t -> t
                        .field("productSlug")
                        .value(query)
                        .boost(12.0f)
                ))
                .should(s -> s.matchPhrase(mp -> mp
                        .field("product_name")
                        .query(query)
                        .slop(1)
                        .boost(8.0f)
                ))
                .should(s -> s.term(t -> t
                        .field("brand.brand_slug")
                        .value(query)
                        .boost(8.0f)
                ))
                .should(s -> s.term(t -> t
                        .field("product_type.product_type_slug")
                        .value(query)
                        .boost(6f)
                ))
                .should(s -> s.term(t -> t
                        .field("collections.collection_slug")
                        .value(query)
                        .boost(4f)
                ))
                .should(s -> s.term(t -> t
                        .field("variant.sku")
                        .value(query)
                        .boost(10.0f)
                ))
                .should(s -> s.multiMatch(mm -> mm
                        .query(query)
                        .fields(
                                "product_name^8",
                                "variant.variant_name^8",
                                "brand.brand_name^6",
                                "product_type.product_type_name^5",
                                "collections.collection_name^4",
                                "short_description^3"
                        )
                        .fuzziness("AUTO")
                        .minimumShouldMatch("75%")
                ))
                .should(s -> s.nested(n -> n
                        .path("attributes")
                        .scoreMode(ChildScoreMode.Max)
                        .query(q -> q.multiMatch(mm -> mm
                                .query(query)
                                .fields(
                                        "attributes.name^2",
                                        "attributes.value^2"
                                )
                        ))
                ))
                .should(s -> s.multiMatch(mm -> mm
                        .query(query)
                        .fields(
                                "variant.variant_name^5",
                                "variant.option_values.*^4"
                        )
                ))
                .minimumShouldMatch("1"));
    }

    private List<SortOptions> sortQuery(String sortBy) {
        return switch (sortBy) {
            case "published_at" -> List.of(
                    SortOptions.of(s -> s.field(f -> f.field("published_at").order(SortOrder.Desc)))
            );
            case "price-ascending" -> List.of(
                    SortOptions.of(s -> s.field(f -> f.field("variant.sale_price").order(SortOrder.Asc))),
                    SortOptions.of(s -> s.field(f -> f.field("published_at").order(SortOrder.Desc)))
            );
            case "price-descending" -> List.of(
                    SortOptions.of(s -> s.field(f -> f.field("variant.sale_price").order(SortOrder.Desc))),
                    SortOptions.of(s -> s.field(f -> f.field("published_at").order(SortOrder.Desc)))
            );
            case "best-selling" -> List.of(
                    SortOptions.of(s -> s.field(f -> f.field("product_stats.sold_count").order(SortOrder.Desc)))
            );
            default -> List.of(
                    SortOptions.of(s -> s.score(sc -> sc.order(SortOrder.Desc)))
            );
        };
    }

    private int discount(VariantSummaryResponse variant) {
        if (variant == null) {
            return 0;
        }
        Long listPrice = variant.getListPrice();
        Long salePrice = variant.getSalePrice();
        int discount = 0;
        if (listPrice != null && salePrice != null && listPrice > 0 && listPrice > salePrice) {
            discount = (int) Math.round((1.0 - (double) salePrice / listPrice) * 100);
        }
        return discount;
    }
}
