package ru.sazhin.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private BigDecimal amount;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock writeLock = readWriteLock.writeLock();
    private final Lock readLock = readWriteLock.readLock();

    public Account() {
    }

    public Account(long id, BigDecimal amount) {
        this.id = id;
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        readLock.lock();
        try {
            return amount;
        } finally {
            readLock.unlock();
        }
    }

    public void changeAmount(BigDecimal diff) {
        writeLock.lock();
        try {
            amount = amount.add(diff);
        } finally {
            writeLock.unlock();
        }
    }

    public Lock getWriteLock() {
        return writeLock;
    }

    public Lock getReadLock() {
        return readLock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id == account.id &&
                Objects.equals(amount, account.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", amount=" + amount +
                '}';
    }
}
