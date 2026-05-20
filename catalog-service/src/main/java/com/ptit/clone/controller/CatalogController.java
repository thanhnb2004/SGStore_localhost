package com.ptit.clone.controller;

import com.ptit.clone.service.IProductQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/catalogs")
public class CatalogController {

    private final IProductQueryService productQueryService;

    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam("q") String q,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "relevance") String sortBy
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(productQueryService.search(q, page, size, sortBy));
    }

    @GetMapping("/best-sellers")
    public ResponseEntity<?> bestSellers(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "10") Integer limit
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(productQueryService.getBestSellers(category, limit));
    }


}
