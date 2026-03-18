package com.biubiu.stock.stockanalyze.utils;

import com.biubiu.stock.stockanalyze.enums.UnitEnum;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumChangeUtis {
    public static String getNumChangeStr(BigDecimal num) {
        String out = "";
        Integer scale = num.precision() - num.scale();
        UnitEnum[] unitEnums = UnitEnum.values();
        for (int i = UnitEnum.values().length - 1; i >= 0; i--) {
            if (scale > unitEnums[i].unitDigit) {
                BigDecimal divisor = BigDecimal.TEN.pow(unitEnums[i].unitDigit);
                BigDecimal finalNum = num.divide(divisor, 2, RoundingMode.HALF_UP);
                out = finalNum.toString() + unitEnums[i].unitName;
                return out;
            }
        }
        return num.toString();
    }
}
