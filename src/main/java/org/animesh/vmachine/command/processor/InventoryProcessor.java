package org.animesh.vmachine.command.processor;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.animesh.vmachine.VendingMachineContext;
import org.animesh.vmachine.command.CommandProcessor;
import org.animesh.vmachine.command.exp.CommandProcessException;
import org.animesh.vmachine.command.exp.UnknownCommandException;
import org.animesh.vmachine.inventory.Inventory;
import org.animesh.vmachine.inventory.Product;
import org.animesh.vmachine.secure.AdminAccess;

@Singleton
@AdminAccess
public class InventoryProcessor extends BaseProcessor {

    final static String CMD_LIST = "LST";
    final static String CMD_ADP = "ADP";
    final static String CMD_ADS = "ADS";

    final CommandProcessor addProductProcessor;
    final CommandProcessor addStockProcessor;
    final Inventory inventory;

    @Inject
    public InventoryProcessor(VendingMachineContext context, Inventory inventory,
            @Named("addProduct") CommandProcessor addProductProcessor,
            @Named("addStock") CommandProcessor addStockProcessor) {
        super(context);
        this.addProductProcessor = addProductProcessor;
        this.addStockProcessor = addStockProcessor;
        this.inventory = inventory;
    }

    @Override
    public void process(String command) throws CommandProcessException {
        super.process(command);

        // List
        if (command.equalsIgnoreCase(CMD_LIST)) {
            List<Product> producsts = inventory.getAllProducts();
            for (Product p : producsts) {
                context.printDebug(String.format("%d\t%s\t%s", p.getAvailableUnits(), p.getCode(),
                        p.getPrice()));
            }
            return;
        }

        // Add product
        if (command.equalsIgnoreCase(CMD_ADP)) {
            context.setCommandProcessor(addProductProcessor);
            return;
        }

        // Add stock
        if (command.equalsIgnoreCase(CMD_ADS)) {
            context.setCommandProcessor(addStockProcessor);
            return;
        }

        throw new UnknownCommandException(command);
    }

    @Override
    public void displayOptions() {
        context.printOptions(CMD_LIST, "List products");
        context.printOptions(CMD_ADP, "Add a new product");
        context.printOptions(CMD_ADS, "Add stock");
        super.displayOptions();
    }

    @Override
    public String name() {
        return "Inventory";
    }
}
