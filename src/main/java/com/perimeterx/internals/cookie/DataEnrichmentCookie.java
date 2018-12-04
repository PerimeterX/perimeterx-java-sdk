package com.perimeterx.internals.cookie;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class DataEnrichmentCookie {
    private JsonNode jsonPayload;
    private boolean isValid;
}