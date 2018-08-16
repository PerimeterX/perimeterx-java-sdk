package com.perimeterx.internals.cookie;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RawCookieData {

    String version;

    String selectedCookie;

}
