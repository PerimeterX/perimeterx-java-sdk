package com.perimeterx.internal.cookie.cookieparser;

import com.perimeterx.internals.cookie.CookieVersion;
import com.perimeterx.internals.cookie.RawCookieData;
import com.perimeterx.internals.cookie.cookieparsers.CookieHeaderParser;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

public class CookieHeaderParserTest {

    private CookieHeaderParser cookieHeaderParser = new CookieHeaderParser();

    private String singleCookieV3 = "25676dd757cb796e1b4252a4d395e7dbdd7b36787ac9d5c884a52b8cc3d79cd5:ZzY2AgkAS3Dhu6BVQAyn5XxQXQIuYLmnklh0gzPTsif/ItN+kQq46jyq0YPXYahfstf/r4V+mkexvyPl4KKLeA==:1000:ZZht8m6lbnBHMeMtIvQKYSbbea6fiuIQyjxLZhgX9L5ODbE73qBBZ6FJSN5+NWCCzCNWnFAWItIA5e+4l3gi2B6ykh//KiDwqr5jw9XHACz4r/XEZvGtUoxUsPGC8sT9rjG1oWe/+omoWqSTAxJlXulcbtbhivy+Mlf+75Z7hT8gVsQr9aWXw1hsc2KlfifN";
    private String singleCookieV1 = "eGPYisZ+3qU=:1000:7igwrWkws2WEaST6gH9QyFzDWzlLFmR8Y50DG2ZUwCNjdvaZ6niEBqqI68whIuA96G481qX48WSV9GGv0bAhIslbNeZUjpbndMJIAF8O4MqnKTHEVzTGzCCKDfHFIlAl3eCvvvhddhZLMYJUI1tA6lWkO5zHQd33O4w7IQ3DPwqKpe7lxQVHSNPA/emFNG6o/g4gz7RTP9FQep6SW8GuEdHXv4a2EIvaqRZlOfyky0vTeS5cgiSblu2neDe4ntB3Xm5s7XN7+mjhPjTGoEfZCA==";


    @Test
    public void testCreateRawCookieDataFromCookieHeader(){
        String cookie = "_px3=" + singleCookieV3 + ";" + "_px=" + singleCookieV1;
        List <RawCookieData> cookieList = new ArrayList<>();
        cookieList.add(new RawCookieData(CookieVersion.V3,singleCookieV3));
        cookieList.add(new RawCookieData(CookieVersion.V1, singleCookieV1));

        List<RawCookieData> rawCookieDataList = cookieHeaderParser.createRawCookieDataList(cookie);
        assertEquals(cookieList, rawCookieDataList);
    }

    @Test
    public void testCreateRawCookieDataFromCookieHeaderAndFail(){
        String cookie = "_px3=" + singleCookieV3 + ";" + "px=" + singleCookieV1;
        List <RawCookieData> cookieList = new ArrayList<>();
        cookieList.add(new RawCookieData(CookieVersion.V3,singleCookieV3));
        cookieList.add(new RawCookieData(CookieVersion.V1, singleCookieV1));

        List <RawCookieData> rawCookieDataList = cookieHeaderParser.createRawCookieDataList(cookie);
        assertNotEquals(cookieList, rawCookieDataList);
    }

}
