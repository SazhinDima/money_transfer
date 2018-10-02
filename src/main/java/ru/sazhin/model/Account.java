package ru.sazhin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@DatabaseTable
public class Account {

    @JsonIgnore
    @DatabaseField(generatedId = true)
    private long id;

    @JsonProperty
    @DatabaseField(foreign=true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private User user;

    @JsonProperty
    @DatabaseField
    private BigDecimal amount;

    @JsonIgnore
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    @JsonIgnore
    private final Lock writeLock = readWriteLock.writeLock();
    @JsonIgnore
    private final Lock readLock = readWriteLock.readLock();

    public Account() {
    }

    public Account(User user, BigDecimal amount) {
        this.user = user;
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
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
