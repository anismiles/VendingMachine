package org.animesh.vmachine.command.processor;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.animesh.vmachine.VendingMachineContext;
import org.animesh.vmachine.command.CommandProcessor;
import org.animesh.vmachine.command.exp.CommandProcessException;
import org.animesh.vmachine.inventory.Inventory;
import org.animesh.vmachine.secure.AdminAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

@Singleton
@AdminAccess
public class AddStockProcessor extends BaseProcessor {
    // LOG
    private static final Logger LOG = LoggerFactory.getLogger(AddStockProcessor.class);

    final static String CMD_STOCK = "STOCK";
    final CommandProcessor inventoryProcessor;
    final Inventory inventory;

    @Inject
    public AddStockProcessor(VendingMachineContext context, Inventory inventory,
            @Named("inventory") CommandProcessor inventoryProcessor) {
        super(context);
        this.inventoryProcessor = inventoryProcessor;
        this.inventory = inventory;
    }

    @Override
    public void process(String command) throws CommandProcessException {
        super.process(command);

        // Stock Product
        try {
            List<String> parts = Lists.newArrayList(Splitter.on(" ").omitEmptyStrings().split(command));
            LOG.debug("parts = {}", parts);

            if (parts.size() != 3) {
                throw new Exception("Invalid command, please try again!");
            }

            if (!CMD_STOCK.equalsIgnoreCase(parts.get(0))) {
                throw new Exception("Invalid command, please try again!");
            }

            final String code = parts.get(1);
            // exits?
            if (!inventory.exists(code)) {
                throw new Exception("Product by code " + code
                        + " was not found in the inventory, you should add this product first.");
            }

            long units = 0;
            try {
                units = Long.parseLong(parts.get(2));
            } catch (NumberFormatException e) {
                throw new Exception("Number of units must be a positive number.");
            }

            inventory.stock(code, units);
            context.printDebug("Successfully stocked " + units + " units of product " + code);

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
        context.printDebug("Type: STOCK <product-code> <number-of-units>");
        context.printDebug("// note: Product Code must be a single word, and number of units must be a positive integer Number.");
        context.printDebug("// e.g. STOCK SPRITE 3");
    }

    @Override
    public String name() {
        return "Stock Product";
    }
}
