package com.mipa.common.utils;

import com.github.pagehelper.PageInfo;

import java.util.List;

public record PageRecord<T>(List<T> datas, Long total, Integer pageNum, Integer pageSize) {
    public static <T>  PageRecord<T> of(List<T> datas, PageInfo pageInfo){
        return new PageRecord<>(datas, pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }
}
