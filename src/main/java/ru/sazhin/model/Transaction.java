package ru.sazhin.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Transaction {

    private long id;
    private Account from;
    private Account to;
    private BigDecimal ammount;

    public Transaction(long id, Account from, Account to, BigDecimal ammount) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.ammount = ammount;
    }

    public long getId() {
        return id;
    }

    public Account getFrom() {
        return from;
    }

    public Account getTo() {
        return to;
    }

    public BigDecimal getAmmount() {
        return ammount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return id == that.id &&
                Objects.equals(from, that.from) &&
                Objects.equals(to, that.to) &&
                Objects.equals(ammount, that.ammount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, from, to, ammount);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", from=" + from +
                ", to=" + to +
                ", ammount=" + ammount +
                '}';
    }
}
