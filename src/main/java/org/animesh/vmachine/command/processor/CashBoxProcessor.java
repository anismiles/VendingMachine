package org.animesh.vmachine.command.processor;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.animesh.vmachine.VendingMachineContext;
import org.animesh.vmachine.Money;
import org.animesh.vmachine.command.CommandProcessor;
import org.animesh.vmachine.command.exp.CommandProcessException;
import org.animesh.vmachine.command.exp.UnknownCommandException;
import org.animesh.vmachine.money.Cash;
import org.animesh.vmachine.money.Cashbox;
import org.animesh.vmachine.secure.AdminAccess;

@Singleton
@AdminAccess
public class CashBoxProcessor extends BaseProcessor {

    final static String CMD_BALANCE = "BAL";
    final static String CMD_BALANCE_EXPLAIN = "EXB";
    final static String CMD_DEPOSIT = "DEP";

    final CommandProcessor cashDepositProcessor;

    final Cashbox cashBox;

    @Inject
    public CashBoxProcessor(VendingMachineContext context, Cashbox cashBox,
            @Named("cashDeposit") CommandProcessor cashDepositProcessor) {
        super(context);
        this.cashDepositProcessor = cashDepositProcessor;
        this.cashBox = cashBox;
    }

    @Override
    public void process(String command) throws CommandProcessException {
        super.process(command);

        // Balance?
        if (command.equalsIgnoreCase(CMD_BALANCE)) {
            context.printDebug("Cashbox balance: " + cashBox.balance());
            return;
        }

        // Explain balance
        if (command.equalsIgnoreCase(CMD_BALANCE_EXPLAIN)) {
            Map<Cash, Long> balance = cashBox.explainBalance();
            context.printDebug("Cashbox balance details:");
            long total = 0;
            for (Map.Entry<Cash, Long> e : balance.entrySet()) {
                long val = e.getValue() * e.getKey().getValue().toCoins();
                total += val;
                context.printDebug(String.format("%s\tx\t%d\t= %s", e.getKey().getDescription(),
                        e.getValue(), Money.parse(val)));
            }
            context.printDebug("Total\t= " + Money.parse(total));
            return;
        }

        // Deposit
        if (command.equalsIgnoreCase(CMD_DEPOSIT)) {
            context.setCommandProcessor(cashDepositProcessor);
            return;
        }

        throw new UnknownCommandException(command);
    }

    @Override
    public void displayOptions() {
        context.printOptions(CMD_DEPOSIT, "Deposit ");
        context.printOptions(CMD_BALANCE, "Show Balance ");
        context.printOptions(CMD_BALANCE_EXPLAIN, "Explain Balance ");
        super.displayOptions();
    }

    @Override
    public String name() {
        return "Cashbox";
    }
}
