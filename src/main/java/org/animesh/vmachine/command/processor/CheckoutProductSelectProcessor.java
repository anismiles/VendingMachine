package org.animesh.vmachine.command.processor;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.animesh.vmachine.VendingMachineContext;
import org.animesh.vmachine.command.exp.CommandProcessException;
import org.animesh.vmachine.inventory.Inventory;
import org.animesh.vmachine.inventory.Product;

@Singleton
public class CheckoutProductSelectProcessor extends BaseProcessor {

    final static String CMD_CHECKOUT = "CKO";
    final CheckoutProcessorFactory checkoutProcessorFactory;
    final Inventory inventory;

    @Inject
    public CheckoutProductSelectProcessor(VendingMachineContext context, Inventory inventory,
            CheckoutProcessorFactory checkoutProcessorFactory) {
        super(context);
        this.checkoutProcessorFactory = checkoutProcessorFactory;
        this.inventory = inventory;
    }

    @Override
    public void process(String command) throws CommandProcessException {
        super.process(command);

        Product product = inventory.get(command);
        if (null != product) {
            context.setCommandProcessor(checkoutProcessorFactory.create(product.getCode()));
            return;
        }

        context.printError("Unknown product, please try again.");
    }

    @Override
    public void displayOptions() {
        List<Product> products = inventory.getAvailableProducts();

        if (products.isEmpty()) {
            context.printDebug("We don't have any product currently!");
            return;
        }
        
        // Display
        context.printDebug("Please select a product code from following available ones: ");
        for (Product p : products) {
            context.printOptions(p.getCode(), p.getPrice());
        }
    }

    @Override
    public void displayHeadline() {
    }

    @Override
    public String name() {
        return "Checkout Select";
    }
}
