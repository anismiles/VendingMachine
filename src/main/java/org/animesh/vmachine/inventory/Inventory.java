package org.animesh.vmachine.inventory;

import java.util.List;


public interface Inventory {

    boolean exists(String code);

    boolean available(String code);

    Product get(String code);

    boolean addProduct(Product product);

    boolean stock(String code, long units);

    boolean unstock(String code);

    List<Product> getAllProducts();

    List<Product> getAvailableProducts();
}
