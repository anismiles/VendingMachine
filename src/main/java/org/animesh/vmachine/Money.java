package org.animesh.vmachine;

import static com.google.common.base.Preconditions.checkArgument;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Pattern;

import org.animesh.vmachine.command.CommandListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class Money.
 * 
 * @author "Animesh Kumar <animesh@strumsoft.com>"
 * 
 */
public final class Money {
    // LOG
    private static final Logger LOG = LoggerFactory.getLogger(CommandListener.class);
    final static NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("EN", "IN")); // Locale INDIA
    final static Pattern mPattern = Pattern.compile("\\d+(\\.\\d{1,2})?"); // 34.01, 34, 34.56 etc.
    final long notes;
    final long coins;

    /**
     * Instantiates a new money.
     *
     * @param notes the notes
     * @param coins the coins
     */
    private Money(long notes, long coins) {
        this((notes * 100) + coins);
    }

    // TODO: Apply flyweight? Ah! too small to bother.
    /**
     * Instantiates a new money.
     *
     * @param coins the coins
     */
    private Money(final long coins) {
        checkArgument(!(coins < 0), "Must be a positive number");
        this.notes = coins / 100;
        this.coins = coins % 100;
    }

    /**
     * Parses the.
     *
     * @param notes the notes
     * @param paise the paise
     * @return the money
     */
    public static Money parse(final long notes, final long paise) {
        return new Money(notes, paise);
    }

    /**
     * Parses the.
     *
     * @param paise the paise
     * @return the money
     */
    public static Money parse(final long paise) {
        return new Money(paise);
    }

    /**
     * Parses the.
     *
     * @param unit the unit
     * @return the money
     * @throws ParseException the parse exception
     */
    public static Money parse(final String unit) throws ParseException {
        checkArgument(mPattern.matcher(unit).matches(), "Failed to parse " + unit);
        return parse(new BigDecimal(unit));
    }

    /**
     * Parses the.
     *
     * @param bd the bd
     * @return the money
     */
    public static Money parse(final BigDecimal bd) {
        long rs = bd.longValue();
        long ps = Math.round(bd.remainder(BigDecimal.ONE) // Remainder
                .multiply(BigDecimal.TEN) // multiply by 10
                .multiply(BigDecimal.TEN) // again by 10 ==>> total: 100
                .doubleValue() // get double, and later will round it
                );
        LOG.debug("bd={}, rs={}, ps={}", new Object[] { bd, rs, ps });
        return new Money(rs, ps);
    }

    /**
     * Gets the notes.
     *
     * @return the notes
     */
    public long getNotes() {
        return notes;
    }

    /**
     * Gets the coins.
     *
     * @return the coins
     */
    public long getCoins() {
        return coins;
    }

    /**
     * To coins.
     *
     * @return the long
     */
    public long toCoins() {
        return (notes * 100) + coins;
    }

    /**
     * Checks if is greater than or equal to.
     *
     * @param o the other
     * @return true, if is greater than or equal to
     */
    public boolean isGreaterThanOrEqualTo(Money o) {
        return this.toCoins() >= o.toCoins();
    }

    /**
     * Adds the.
     *
     * @param o the other
     * @return the money
     */
    public Money add(Money o) {
        return new Money(this.toCoins() + o.toCoins());
    }

    /**
     * Subtract.
     *
     * @param o the other
     * @return the money
     */
    public Money subtract(Money o) {
        return new Money(this.toCoins() - o.toCoins());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (coins ^ (coins >>> 32));
        result = prime * result + (int) (notes ^ (notes >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Money other = (Money) obj;
        if (coins != other.coins)
            return false;
        if (notes != other.notes)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return nf.format(Double.parseDouble(notes + "." + coins));
    }
}
