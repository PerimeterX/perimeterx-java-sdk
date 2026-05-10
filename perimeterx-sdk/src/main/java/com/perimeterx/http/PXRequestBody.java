package com.perimeterx.http;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.InputStream;

@Getter
@AllArgsConstructor
public class PXRequestBody {
    private InputStream inputStream;
}
