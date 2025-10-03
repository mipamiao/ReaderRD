package com.mipa.utils;

import java.util.UUID;

public class IdUtil {
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
