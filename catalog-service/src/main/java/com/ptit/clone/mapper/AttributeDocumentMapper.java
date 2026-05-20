package com.ptit.clone.mapper;

import com.ptit.clone.dtos.response.AttributeSummaryListResponse;
import com.ptit.clone.dtos.response.AttributeSummaryResponse;
import com.ptit.clone.entity.AttributeDocument;
import com.ptit.clone.messaging.event.AttributePayload;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AttributeDocumentMapper {

    public AttributeSummaryResponse toAttributeSummaryResponse(AttributeDocument document) {
        if (document == null) {
            return null;
        }

        return AttributeSummaryResponse.builder()
                .name(document.getName())
                .value(document.getValue())
                .build();
    }

    public AttributeSummaryListResponse toAttributeSummaryListResponse (List<AttributeDocument> documents){
        List<AttributeSummaryResponse> attributeSummaryResponses = new ArrayList<>();
        for(AttributeDocument attribute : documents){
            attributeSummaryResponses.add(toAttributeSummaryResponse(attribute));
        }
        return AttributeSummaryListResponse.builder()
                .items(attributeSummaryResponses)
                .build();
    }
}
