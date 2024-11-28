package org.example;

import java.math.BigDecimal;

public class ConcurrentBankExample {
    public static void main(String[] args) {
        ConcurrentBank bank = new ConcurrentBank();

        BankAccount account1 = bank.createAccount(BigDecimal.valueOf(1000));
        BankAccount account2 = bank.createAccount(BigDecimal.valueOf(500));

        Thread transferThread1 = new Thread(() -> {
            try {
                bank.transfer(account1, account2, BigDecimal.valueOf(200));
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка при переводе: " + e.getMessage());
            }
        });

        Thread transferThread2 = new Thread(() -> {
            try {
                bank.transfer(account2, account1, BigDecimal.valueOf(700));
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка при переводе: " + e.getMessage());
            }
        });

        transferThread1.start();
        transferThread2.start();

        try {
            transferThread1.join();
            transferThread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Общий баланс: " + bank.getTotalBalance());
    }
}