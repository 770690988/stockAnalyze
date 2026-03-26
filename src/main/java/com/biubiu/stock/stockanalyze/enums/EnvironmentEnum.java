package com.biubiu.stock.stockanalyze.enums;

public enum EnvironmentEnum {
    DEVELOP("develop"),
    PRODUCT("product");

    public String environment;

    EnvironmentEnum(String environment) {
        this.environment = environment;
    }
}
