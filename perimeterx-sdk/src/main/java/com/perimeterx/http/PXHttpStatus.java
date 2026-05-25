package com.perimeterx.http;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PXHttpStatus {
    private int statusCode;
    private String reasonPhrase;
}