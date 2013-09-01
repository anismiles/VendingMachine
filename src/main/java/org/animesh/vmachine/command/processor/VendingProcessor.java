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
import org.animesh.vmachine.money.Cashbox;

@Singleton
public class VendingProcessor extends BaseProcessor {
    // LOG
    // private static final Logger LOG = LoggerFactory.getLogger(VendingProcessor.class);

    final static String CMD_LIST = "LST";
    final static String CMD_CHECKOUT = "CKO";

    final CommandProcessor checkoutSelectProcessor;
    final Inventory inventory;
    final Cashbox cashbox;

    @Inject
    public VendingProcessor(VendingMachineContext context, Inventory inventory, Cashbox cashbox,
            @Named("checkoutSelect") CommandProcessor checkoutSelectProcessor) {
        super(context);
        this.checkoutSelectProcessor = checkoutSelectProcessor;
        this.inventory = inventory;
        this.cashbox = cashbox;
    }

    @Override
    public void process(String command) throws CommandProcessException {
        super.process(command);

        // List products
        if (command.equalsIgnoreCase(CMD_LIST)) {
            List<Product> products = inventory.getAvailableProducts();
            // Display
            for (Product p : products) {
                context.printDebug(String.format("%s\t%s", p.getCode(), p.getPrice()));
            }

            if (products.isEmpty()) {
                context.printDebug("We don't have any product currently!");
            }
            return;
        }

        // checkout
        if (command.equalsIgnoreCase(CMD_CHECKOUT)) {
            context.setCommandProcessor(checkoutSelectProcessor);
            return;
        }

        throw new UnknownCommandException(command);
    }

    @Override
    public void displayOptions() {
        context.printOptions(CMD_LIST, "List products");
        context.printOptions(CMD_CHECKOUT, "Checkout product");
        super.displayOptions();
    }

    @Override
    public String name() {
        return "Vending";
    }
}
