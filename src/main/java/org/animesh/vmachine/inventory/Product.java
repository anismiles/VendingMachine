package org.animesh.vmachine.inventory;

import static com.google.common.base.Preconditions.checkArgument;

import org.animesh.vmachine.Money;

public class Product {

    final static Money lower = Money.parse(1, 0); // Rs. 1
    final static Money upper = Money.parse(100, 0); // Rs. 100

    final String code;
    Money price;
    long availableUnits;

    public Product(String code, Money price) {
        // validate price
        checkArgument(price.toCoins() > lower.toCoins() && upper.toCoins() > price.toCoins(),
                "Price must be between " + lower + " and " + upper + ".");
        checkArgument(code.length() > 3, "Product code must be at least 4 characters long.");
        this.code = code;
        this.price = price;
        this.availableUnits = 0L;
    }

    public String getCode() {
        return code;
    }

    public Money getPrice() {
        return price;
    }

    public void setPrice(Money price) {
        this.price = price;
    }

    public long getAvailableUnits() {
        return availableUnits;
    }

    public void addNumberOfUnits(long units) {
        this.availableUnits += units;
    }

    public void reduceNumberOfUnits(long units) {
        this.availableUnits -= units;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((code == null) ? 0 : code.hashCode());
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
        Product other = (Product) obj;
        if (code == null) {
            if (other.code != null)
                return false;
        } else if (!code.equals(other.code))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Product [code=" + code + ", price=" + price + ", units=" + availableUnits + "]";
    }

}
