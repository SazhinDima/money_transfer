package ru.sazhin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.math.BigDecimal;
import java.util.Objects;

@DatabaseTable
public class Transaction {

    @JsonIgnore
    @DatabaseField(generatedId = true)
    private long id;

    @JsonProperty
    @DatabaseField(foreign=true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private Account from;

    @JsonProperty
    @DatabaseField(foreign=true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private Account to;

    @JsonProperty
    @DatabaseField
    private BigDecimal amount;

    public Transaction() {
    }

    public Transaction(Account from, Account to, BigDecimal amount) {
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
