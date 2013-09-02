package org.animesh.vmachine.money;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.animesh.vmachine.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

public class InMemoryCashboxImpl implements Cashbox {
    // LOG
    private static final Logger LOG = LoggerFactory.getLogger(InMemoryCashboxImpl.class);

    private final boolean cashboxInit;
    private final Map<Cash, Long> box;

    @Inject
    public InMemoryCashboxImpl(@Named("cashbox.init") boolean cashboxInit) {
        this.cashboxInit = cashboxInit;
        // keep it ordered
        this.box = new TreeMap<Cash, Long>(Collections.reverseOrder());
        // populate with 0
        for (Cash m : Cash.orderedValues()) {
            box.put(m, 0L);
        }
    }

    @PostConstruct
    public void init() {
        if (!cashboxInit) {
            LOG.info("Cashbox is not initialized with dummy data.");
            return;
        }
        deposit(Cash.N100, 10);
        deposit(Cash.N20, 10);
        deposit(Cash.N5, 100);
        deposit(Cash.C10, 180);
        deposit(Cash.C5, 18);
        deposit(Cash.C2, 18);
        deposit(Cash.C1, 10);
    }

    @Override
    public void deposit(final Cash cash, final long units) {
        checkArgument(units > 0, "Number of units must be a positive number.");
        box.put(cash, box.get(cash) + units);
    }

    @Override
    public void deposit(Cash... units) {
        for (final Cash m : units) {
            deposit(m, 1);
        }
    }

    @Override
    public Money balance() {
        long amount = 0L;
        // iterate and sum
        for (final Map.Entry<Cash, Long> entry : box.entrySet()) {
            final Cash moolah = entry.getKey();
            final long count = entry.getValue();
            amount += count * moolah.getValue().toCoins();
        }
        return Money.parse(amount);
    }

    @Override
    public Map<Cash, Long> explainBalance() {
        return Collections.unmodifiableMap(Maps.filterValues(box, new Predicate<Long>() {
            // Filter all notes/coins that have no units
            @Override
            public boolean apply(Long input) {
                return input > 0L;
            }
        }));
    }

    private void withdraw(final Cash cash, final long units) {
        checkArgument(units > 0, "Number of units must be a positive number.");
        box.put(cash, box.get(cash) - units);
    }

    @Override
    public boolean canWithdraw(Money money) {
        return calculateWithdrawCashParts(money) != null;
    }

    @Override
    public Map<Cash, Long> withdraw(Money money) {
        Map<Cash, Long> withdraw = calculateWithdrawCashParts(money);
        if (null == withdraw) {
            return null;
        }
        for (final Map.Entry<Cash, Long> entry : withdraw.entrySet()) {
            final Cash moolah = entry.getKey();
            final long count = entry.getValue();
            withdraw(moolah, count);
        }

        return withdraw;
    }

    private Map<Cash, Long> calculateWithdrawCashParts(Money money) {
        if (money.toCoins() > balance().toCoins()) {
            return null;
        }

        Map<Cash, Long> withdraw = new TreeMap<Cash, Long>(Collections.reverseOrder());
        long remainingAmount = money.toCoins();

        // iterate over moolah in orderly fashion,
        for (Cash moolah : Cash.orderedValues()) {
            // how many units we have?
            final long unitsHave = box.get(moolah);
            // should skip?
            if (unitsHave == 0L) {
                continue;
            }

            // how many units we need?
            final long unitsRequired = (long) Math.floor(remainingAmount / moolah.getValue().toCoins());

            // should skip?
            if (unitsRequired == 0L) {
                continue;
            }

            // pick minimum of unitsRequired and unitsHave
            final long unitsPicked = Math.min(unitsRequired, unitsHave);
            final long unitsValue = unitsPicked * moolah.getValue().toCoins();

            long oldRemainingAmount = remainingAmount; // logging
            // update remainingAmount
            remainingAmount = remainingAmount - unitsValue;
            // add to withdraw map
            withdraw.put(moolah, unitsPicked);

            LOG.debug("{} - ({} x {}) => {}, {}", new Object[] { Money.parse(oldRemainingAmount),
                    unitsPicked, moolah, Money.parse(remainingAmount), withdraw });
        }

        if (remainingAmount == 0L) {
            return withdraw;
        }

        LOG.debug("{}", Money.parse(remainingAmount));
        return null;
    }

    @Override
    public String toString() {
        return "MoneyBox =>> " + box;
    }
    //
    // public static void main(String args[]) {
    // InMemoryCashboxImpl box = new InMemoryCashboxImpl();
    // box.deposit(Cash.N100, 10);
    // box.deposit(Cash.N20, 10);
    // box.deposit(Cash.N5, 100);
    // box.deposit(Cash.C10, 180);
    // box.deposit(Cash.C5, 18);
    // box.deposit(Cash.C2, 18);
    // box.deposit(Cash.C1, 0);
    //
    // System.out.println(box);
    // System.out.println(box.balance());
    // System.out.println(box.balance().toCoins());
    // // System.out.println(box.canWithdraw(new Money(20, 7)));
    // box.withdraw(Money.parse(209, 84));
    // }
}
