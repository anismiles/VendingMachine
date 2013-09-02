package org.animesh.vmachine.money;

import java.util.Arrays;
import java.util.List;

import org.animesh.vmachine.Money;

public enum Cash {
    // @formatter:off
    // Coins
    C1(Money.parse(0, 1L), "1 Paise"), 
    C2(Money.parse(0, 2L), "2 Paise"),
    C5(Money.parse(0, 5L), "5 Paise"), 
    C10(Money.parse(0, 10L), "10 Paise"),

    // Notes
    N5(Money.parse(5L, 0), "5 Rupees"), 
    N10(Money.parse(10L, 0), "10 Rupees"), 
    N20(Money.parse(20L, 0), "20 Rupees"), 
    N50(Money.parse(50L, 0), "50 Rupees"), 
    N100(Money.parse(100L, 0), "100 Rupees");
    // @formatter:on

    // Helps with ordered iteration, and selection of coins/notes for withdrawal
    private static final List<Cash> orderedValues = Arrays.asList(N100, N50, N20, N10, N5, C10, C5, C2, C1);

    private final String description;
    private final Money value;

    private Cash(Money value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public Money getValue() {
        return value;
    }

    public static List<Cash> orderedValues() {
        return orderedValues;
    }

    public static Cash getByName(final String name) {
        for (final Cash m : Cash.values()) {
            if (name.equalsIgnoreCase(m.toString())) {
                return m;
            }
        }
        return null;
    }

}
