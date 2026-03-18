package com.biubiu.stock.stockanalyze.enums;

public enum UnitEnum {

    HUNDRED("百", 2),
    TEN_THOUSAND("万", 4),
    HUNDRED_MILLION("亿", 8);
    // 单位名称
    public String unitName;

    // 单位进制位数
    public Integer unitDigit;

    UnitEnum(String unitName, Integer unitDigit) {
        this.unitName = unitName;
        this.unitDigit = unitDigit;
    }
}
