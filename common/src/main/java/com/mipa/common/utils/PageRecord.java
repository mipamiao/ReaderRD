package com.mipa.common.utils;

import com.github.pagehelper.PageInfo;

import java.util.List;

import java.util.List;

public class PageRecord<T> {
    private  List<T> datas;
    private  Long total;
    private  Integer pageNumber;
    private  Integer pageSize;


    public PageRecord() {} ;
    // 构造函数
    public PageRecord(List<T> datas, Long total, Integer pageNumber, Integer pageSize) {
        this.datas = datas;
        this.total = total;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    // 工厂方法
    public static <T> PageRecord<T> of(List<T> datas, PageInfo pageInfo) {
        return new PageRecord<>(datas, pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    // Getter 方法
    public List<T> getDatas() {
        return datas;
    }

    public Long getTotal() {
        return total;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    // 可选：重写 toString/equals/hashCode，使行为与 record 更接近
    @Override
    public String toString() {
        return "PageRecord{" +
                "datas=" + datas +
                ", total=" + total +
                ", pageNumber=" + pageNumber +
                ", pageSize=" + pageSize +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageRecord<?> that)) return false;
        return java.util.Objects.equals(datas, that.datas)
                && java.util.Objects.equals(total, that.total)
                && java.util.Objects.equals(pageNumber, that.pageNumber)
                && java.util.Objects.equals(pageSize, that.pageSize);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(datas, total, pageNumber, pageSize);
    }
}

