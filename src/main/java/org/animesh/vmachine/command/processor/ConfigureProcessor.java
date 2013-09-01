package org.animesh.vmachine.command.processor;

import javax.inject.Inject;
import javax.inject.Named;

import org.animesh.vmachine.VendingMachineContext;
import org.animesh.vmachine.command.CommandProcessor;
import org.animesh.vmachine.command.exp.CommandProcessException;
import org.animesh.vmachine.command.exp.UnknownCommandException;
import org.animesh.vmachine.secure.AdminAccess;

@AdminAccess
public class ConfigureProcessor extends BaseProcessor {

    final CommandProcessor cashBoxProcessor;
    final CommandProcessor inventoryProcessor;

    @Inject
    public ConfigureProcessor(VendingMachineContext context,
            @Named("cashbox") CommandProcessor cashBoxProcessor,
            @Named("inventory") CommandProcessor inventoryProcessor) {
        super(context);
        this.cashBoxProcessor = cashBoxProcessor;
        this.inventoryProcessor = inventoryProcessor;
    }

    @Override
    public void process(String command) throws CommandProcessException {
        super.process(command);

        // Deposit cash
        if (command.equalsIgnoreCase("CSB")) {
            context.setCommandProcessor(cashBoxProcessor);
            return;
        }

        // Inventory
        if (command.equalsIgnoreCase("INV")) {
            context.setCommandProcessor(inventoryProcessor);
            return;
        }

        throw new UnknownCommandException(command);
    }

    @Override
    public void displayOptions() {
        context.printOptions("CSB", "Manage cashbox");
        context.printOptions("INV", "Manage inventory");
        super.displayOptions();
    }

    @Override
    public String name() {
        return "Configuration";
    }
}
