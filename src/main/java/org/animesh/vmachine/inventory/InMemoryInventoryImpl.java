package org.animesh.vmachine.inventory;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.animesh.vmachine.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@Singleton
public class InMemoryInventoryImpl implements Inventory {
    // LOG
    private static final Logger LOG = LoggerFactory.getLogger(InMemoryInventoryImpl.class);

    final boolean inventoryInit;
    final Map<String, Product> products;

    @Inject
    public InMemoryInventoryImpl(@Named("inventory.init") boolean inventoryInit) {
        this.products = new HashMap<String, Product>();
        this.inventoryInit = inventoryInit;
    }

    @PostConstruct
    public void init() {
        if (!inventoryInit) {
            LOG.info("Inventory is not initialized with dummy data.");
            return;
        }

        addProduct(new Product("COKE", Money.parse(5, 75)));
        addProduct(new Product("PEPSI", Money.parse(5, 50)));
        addProduct(new Product("LIMCA", Money.parse(4, 45)));
        stock("COKE", 10);
        stock("COKE", 1);
        stock("LIMCA", 7);
        stock("PEPSI", 7);
    }

    @Override
    public boolean exists(final String code) {
        return products.containsKey(code);
    }

    @Override
    public boolean available(final String code) {
        Product product = get(code);
        return null != product && product.getAvailableUnits() > 0L;
    }

    @Override
    public Product get(final String code) {
        return products.get(code);
    }

    @Override
    public List<Product> getAllProducts() {
        return Collections.unmodifiableList(new ArrayList<Product>(products.values()));
    }

    @Override
    public List<Product> getAvailableProducts() {
        return Lists.newArrayList(
                // filter
                Iterables.filter(getAllProducts(), new Predicate<Product>() {
                    @Override
                    public boolean apply(Product product) {
                        return product.getAvailableUnits() > 0;
                    }
                }));
    }

    @Override
    public boolean addProduct(final Product product) {
        if (!exists(product.getCode())) {
            products.put(product.getCode(), product);
            return true;
        }

        return false;
    }

    @Override
    public boolean stock(final String code, final long units) {
        checkArgument(units > 0, "Number of units must be a positive number.");
        Product product = get(code);
        if (null != product) {
            product.addNumberOfUnits(units);
            return true;
        }

        return false;
    }

    @Override
    public boolean unstock(final String code) {
        return unstock(code, 1L);
    }

    private boolean unstock(final String code, final long units) {
        checkArgument(units > 0, "Number of units must be a positive number.");
        Product product = get(code);
        if (null != product) {
            product.reduceNumberOfUnits(units);
            return true;
        }

        return false;
    }
}
