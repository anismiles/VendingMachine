package org.animesh.vmachine.command.processor;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.animesh.vmachine.VendingMachineContext;
import org.animesh.vmachine.Money;
import org.animesh.vmachine.command.CommandProcessor;
import org.animesh.vmachine.command.exp.CommandProcessException;
import org.animesh.vmachine.inventory.Inventory;
import org.animesh.vmachine.inventory.Product;
import org.animesh.vmachine.secure.AdminAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

@Singleton
@AdminAccess
public class AddProductProcessor extends BaseProcessor {
    // LOG
    private static final Logger LOG = LoggerFactory.getLogger(AddProductProcessor.class);

    final static String CMD_ADD = "ADD";
    final CommandProcessor inventoryProcessor;
    final Inventory inventory;

    @Inject
    public AddProductProcessor(VendingMachineContext context, Inventory inventory,
            @Named("inventory") CommandProcessor inventoryProcessor) {
        super(context);
        this.inventoryProcessor = inventoryProcessor;
        this.inventory = inventory;
    }

    @Override
    public void process(String command) throws CommandProcessException {
        super.process(command);

        // Add Product
        try {
            List<String> parts = Lists.newArrayList(Splitter.on(" ").omitEmptyStrings().split(command));
            LOG.debug("parts = {}", parts);

            if (parts.size() != 3) {
                throw new Exception("Invalid command, please try again!");
            }

            if (!CMD_ADD.equalsIgnoreCase(parts.get(0))) {
                throw new Exception("Invalid command, please try again!");
            }

            String code = parts.get(1);
            String price = parts.get(2);
            Product p = new Product(code, Money.parse(price));
            inventory.addProduct(p);

            context.printDebug("Successfully added " + p.getCode() + " with price " + p.getPrice());

            // Go back to inventory processor
            context.setCommandProcessor(inventoryProcessor);
        }
        // Exception?
        catch (Exception e) {
            context.printError("Opps! " + e.getMessage());
        }
    }

    @Override
    public void displayHeadline() {
    }

    @Override
    public void displayOptions() {
        context.printDebug("Type: ADD <product-code> <product-price>");
        context.printDebug("// note: Product Code must be a single word with at least 4 characters, and Product Price must be formatted like Rupee.Paise, like 3.45, or 2.01, or 0.90 etc.");
        context.printDebug("// e.g. ADD SPRITE 3.45");
    }

    @Override
    public String name() {
        return "Add Product";
    }
}
