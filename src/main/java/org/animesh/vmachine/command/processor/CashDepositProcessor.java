package org.animesh.vmachine.command.processor;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.animesh.vmachine.VendingMachineContext;
import org.animesh.vmachine.command.CommandProcessor;
import org.animesh.vmachine.command.exp.CommandProcessException;
import org.animesh.vmachine.money.Cash;
import org.animesh.vmachine.money.Cashbox;
import org.animesh.vmachine.secure.AdminAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

@Singleton
@AdminAccess
public class CashDepositProcessor extends BaseProcessor {
    // LOG
    private static final Logger LOG = LoggerFactory.getLogger(CashDepositProcessor.class);

    final static String CMD_CASH = "CASH";
    final CommandProcessor cashBoxProcessor;
    final Cashbox cashBox;

    @Inject
    public CashDepositProcessor(VendingMachineContext context, Cashbox cashBox,
            @Named("cashbox") CommandProcessor cashBoxProcessor) {
        super(context);
        this.cashBoxProcessor = cashBoxProcessor;
        this.cashBox = cashBox;
    }

    @Override
    public void process(String command) throws CommandProcessException {
        super.process(command);

        // Deposit cash
        try {
            List<String> parts = Lists.newArrayList(Splitter.on(" ").omitEmptyStrings().split(command));
            LOG.debug("parts = {}", parts);

            if (parts.size() != 3) {
                throw new Exception("Invalid command, please try again!");
            }

            if (!CMD_CASH.equalsIgnoreCase(parts.get(0))) {
                throw new Exception("Invalid command, please try again!");
            }

            String currency = parts.get(1);
            Cash cash = Cash.getByName(currency.toUpperCase());
            // cash?
            if (null == cash) {
                throw new Exception("Unknown currency " + currency);
            }

            // units?
            long units = 0;
            try {
                units = Long.parseLong(parts.get(2));
            } catch (NumberFormatException e) {
                throw new Exception("Number of units must be a positive number.");
            }

            cashBox.deposit(cash, units);
            context.printDebug("Successfully deposited " + units + " units of " + cash.getValue());

            // Go back to cashbox processor
            context.setCommandProcessor(cashBoxProcessor);
        }
        // Exception?
        catch (Exception e) {
            context.printError("Opps! " + e.getMessage());
        }
    }

    @Override
    public void displayOptions() {
        context.printDebug("Type: CASH <currency> <number-of-units>");
        context.printDebug("// note: Number of units must be a positive integer Number, and Currency must be one of the follwoing:");
        for (Cash cash : Cash.orderedValues()) {
            context.printOptions(cash.toString(), cash.getDescription());
        }
        context.printDebug("// e.g. CASH N100 3");
    }

    @Override
    public void displayHeadline() {
        // TODO: Nothing
    }

    @Override
    public String name() {
        return "Deposit Cash";
    }
}
