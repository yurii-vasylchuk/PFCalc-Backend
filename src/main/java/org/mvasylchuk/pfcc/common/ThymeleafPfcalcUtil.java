package org.mvasylchuk.pfcc.common;

import java.math.BigDecimal;
import java.math.MathContext;

public class ThymeleafPfcalcUtil {
    public String strokeDasharray(String radiusStr, BigDecimal value, BigDecimal maxValue) {
        BigDecimal radius = new BigDecimal(radiusStr);
        BigDecimal circumference = radius.multiply(new BigDecimal(2)).multiply(new BigDecimal(Math.PI));
        BigDecimal arc = circumference.multiply(value).divide(maxValue, MathContext.DECIMAL128);

        return "%f %f".formatted(arc, circumference.subtract(arc));
    }
}
