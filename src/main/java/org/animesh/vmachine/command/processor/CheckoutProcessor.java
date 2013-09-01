package org.animesh.vmachine.command.processor;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.animesh.vmachine.VendingMachineContext;
import org.animesh.vmachine.Money;
import org.animesh.vmachine.command.CommandProcessor;
import org.animesh.vmachine.command.exp.CommandProcessException;
import org.animesh.vmachine.inventory.Inventory;
import org.animesh.vmachine.inventory.Product;
import org.animesh.vmachine.money.Cash;
import org.animesh.vmachine.money.Cashbox;

import com.google.inject.assistedinject.Assisted;

@Singleton
public class CheckoutProcessor extends BaseProcessor {

    final static String CMD_CHECKOUT = "CKO";
    final CommandProcessor homeProcessor;
    final CommandProcessor checkoutSelectProcessor;
    final Inventory inventory;
    final Cashbox cashbox;
    final Product product;

    private Money deposited = null;

    @Inject
    public CheckoutProcessor(@Assisted String productCode, VendingMachineContext context,
            Inventory inventory, Cashbox cashbox, @Named("home") CommandProcessor homeProcessor,
            @Named("checkoutSelect") CommandProcessor checkoutSelectProcessor) {
        super(context);
        this.checkoutSelectProcessor = checkoutSelectProcessor;
        this.homeProcessor = homeProcessor;
        this.inventory = inventory;
        this.cashbox = cashbox;
        this.product = inventory.get(productCode);
        // deposited
        this.deposited = Money.parse(0);
    }

    @Override
    public void process(String command) throws CommandProcessException {
        super.process(command);

        Cash cash = Cash.getByName(command.toUpperCase());
        if (null == cash) {
            context.printDebug("Unknown currency option, please try again.");
            return;
        }

        // Add to deposited
        deposited = deposited.add(cash.getValue());

        // Check if deposited enough?
        if (deposited.isGreaterThanOrEqualTo(product.getPrice())) {
            Money changeToBeReturned = deposited.subtract(product.getPrice());

            // Can cashbox return this amount?
            if (!cashbox.canWithdraw(changeToBeReturned)) {
                context.printError("Opps! We don't have change to refund to you " + changeToBeReturned);
                context.printDebug("Please collect your deposit and try with smaller notes/coins. Thanks!");

                // Flush
                deposited = Money.parse(0);
                return;
            }

            // DO Purchase

            // TODO: TRANSACTION - start
            Map<Cash, Long> withdrawalCashParts = cashbox.withdraw(changeToBeReturned);
            inventory.unstock(product.getCode());
            // TODO: TRANSACTION - stop

            // Disburse remaining amount
            context.printDebug("Success!!!");
            context.printDebug("Please collect your " + product.getCode() + ", and refund "
                    + changeToBeReturned);
            for (Map.Entry<Cash, Long> e : withdrawalCashParts.entrySet()) {
                context.printDebug(String.format("%d\tx\t%s", e.getValue(), e.getKey().getDescription()));
            }

            // Home?
            context.setCommandProcessor(homeProcessor);
        }
    }

    @Override
    public void displayOptions() {
        if (null == product || product.getAvailableUnits() == 0) {
            context.printError("Product by code " + product.getCode() + " is unavailable.");
            context.setCommandProcessor(checkoutSelectProcessor);
            return;
        }

        if (deposited.toCoins() > 0) {
            context.printDebug("So far you have deposited '" + deposited + "'. You have to pay '"
                    + product.getPrice().subtract(deposited) + "' more.");
        }

        context.printDebug("To pay, please select a cash option: ");
        // List of options
        for (Cash cash : Cash.orderedValues()) {
            context.printOptions(cash.toString(), cash.getDescription());
        }
    }

    @Override
    public void displayHeadline() {
        displayHeadline(product.getCode() + " @ " + product.getPrice());
    }

    @Override
    public String name() {
        return "Checkout";
    }
}
