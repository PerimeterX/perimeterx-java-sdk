package com.perimeterx.http;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PXHttpHeader {
    private final String name;
    private final String value;
}
