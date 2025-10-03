package com.mipa.common.utils;

import org.springframework.beans.BeanUtils;

public class CopyProperties {
    public static <SrcT, DstT> DstT run(SrcT src, Class<DstT> dstClass) {
        try {
            DstT dst = dstClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(src, dst);
            return dst;
        } catch (Exception e) {
            throw new RuntimeException("对象拷贝失败", e);
        }
    }

}
