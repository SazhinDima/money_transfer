package ru.sazhin.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(optional=false)
    private Account from;

    @ManyToOne(optional=false)
    private Account to;

    @Column
    private BigDecimal amount;

    public Transaction() {
    }

    public Transaction(long id, Account from, Account to, BigDecimal amount) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.amount = amount;
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

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return id == that.id &&
                Objects.equals(from, that.from) &&
                Objects.equals(to, that.to) &&
                Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, from, to, amount);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", from=" + from +
                ", to=" + to +
                ", ammount=" + amount +
                '}';
    }
}
