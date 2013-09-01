package org.animesh.vmachine.money;

import java.util.Map;

import org.animesh.vmachine.Money;

public interface Cashbox {

    public void deposit(Cash cash, long count);

    public void deposit(Cash... cashes);

    public Map<Cash, Long> explainBalance();

    public Money balance();

    boolean canWithdraw(Money money);

    Map<Cash, Long> withdraw(Money money);
}
