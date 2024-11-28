package org.example;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentBank {

    private final ConcurrentHashMap<Integer, BankAccount> accounts = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(1);

    public BankAccount createAccount(BigDecimal initialBalance) {
        int accountNumber = counter.getAndIncrement();
        BankAccount account = new BankAccount(accountNumber, initialBalance);
        accounts.put(accountNumber, account);
        return account;
    }

    public void transfer(BankAccount from, BankAccount to, BigDecimal amount) {
        BankAccount firstLock = from.getAccountNumber() < to.getAccountNumber() ? from : to;
        BankAccount secondLock = from.getAccountNumber() < to.getAccountNumber() ? to : from;

        firstLock.getLock().lock();
        secondLock.getLock().lock();
        try {
            if (from.withdraw(amount)) {
                to.deposit(amount);
                System.out.println("Перевод " + amount + " с счета " + from.getAccountNumber() +
                        " на счет " + to.getAccountNumber() + " завершен.");
            } else {
                System.out.println("Недостаточно средств на счете " + from.getAccountNumber() + " для перевода.");
            }
        } finally {
            secondLock.getLock().unlock();
            firstLock.getLock().unlock();
        }
    }

    public BigDecimal getTotalBalance() {
        return accounts.values().stream()
                .map(BankAccount::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
