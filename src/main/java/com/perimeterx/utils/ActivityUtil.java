package com.perimeterx.utils;

import com.perimeterx.models.activities.ActivityHeader;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ActivityUtil {
    public static List<ActivityHeader>getActivityHeaders(Map<String, String> headers, Set<String> sensitiveHeaders) {
        if (headers == null || headers.isEmpty()) {
            return null;
        }
        return headers.entrySet().stream()
                .filter(entry -> !sensitiveHeaders.contains(entry.getKey()))
                .map(entry -> new ActivityHeader(entry.getKey(), entry.getValue())).collect(Collectors.toList());

    }
}
