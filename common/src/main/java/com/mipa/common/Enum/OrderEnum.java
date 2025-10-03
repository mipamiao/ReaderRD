package com.mipa.common.Enum;

public enum OrderEnum {
    CHAPTER_COUNT("chapter_count"),
    CREATED_AT("created_at"),
    UPDATED_AT("updated_at"),
    CHAPTER_ORDER("chapter_order"),

    ASC("ASC"),
    DESC("DESC");


    private final String column;

    OrderEnum(String column) {
        this.column = column;
    }

    public String getColumn() {
        return column;
    }
}
