package ru.sazhin.utils;

import java.math.BigDecimal;

public final  class Preconditions {

    private Preconditions() {}

    public static void checkNotNegative(BigDecimal value, String errorMessage) {
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
